Components.utils.import("resource://gre/modules/AddonManager.jsm");

var authentication = true;
var confidentiality = true;
var timeStamping = false;

function auth(){
        authentication = !authentication;
        Components.utils.reportError("auth: "+ authentication);
    }

function conf(){
    confidentiality = !confidentiality;
    Components.utils.reportError("conf: "+ confidentiality);
}

function time(){
    timeStamping = !timeStamping;
    Components.utils.reportError("time: "+ timeStamping);
}


var AESecure = {

  LiveConnect : {},
    
    encrypt : function(){

        // Se nenhuma opção escolinha não efectuamos nenhuma operação
        if(!authentication && !confidentiality && !timeStamping){
            return;
        }

      // Bloqueia o butão de Cifra  
      var button = document.getElementById("button-aesecure-encrypt");
      button.setAttribute("disabled", "true");

      // Copia o conteudo Digitado
      var editor = GetCurrentEditor();  
      //var editor_type = GetCurrentEditorType();  
      var texto = editor.outputToString('text/html', 2);
      

      // Codifica o conteudo em Base64
      //var encoded = Base64.encode(texto);

      // Localiza o local onde o Plugin esta Instalado
      AddonManager.getAddonByID("artur.balanuta@ist.utl.pt", function(addon) {
        var addonLocation = new String(addon.getResourceURI("").QueryInterface(Components.interfaces.nsIFileURL).file.path);

        // Localização da directoria de Jars
        var javaClass = addonLocation + "/chrome/content/java/";


        // Grava a messagem para um ficheiro de texto
        var fileIN = Components.classes["@mozilla.org/file/local;1"] 
        .createInstance(Components.interfaces.nsILocalFile); 
        fileIN.initWithPath( javaClass + "in/" + "text.in" );
        FileManager.Write(fileIN, texto);

        // Acrescentar extençoes
        // TODO


        //Inicializa os parametros do Executavel para envocar o Script
        var exe = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);
        exe.initWithPath("/");
        exe.append("bin");
        exe.append("sh");
                          
        var run = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
        run.init(exe);       

        // Modo e operaçẽs criptograficas
        var mode = 0;
        if(authentication){
            mode = 1;
        }
        if(confidentiality){
            mode = 2;
        }
        if(authentication && confidentiality){
            mode = 3;
        }
        if(timeStamping){
            mode = 4;
        }
        if(timeStamping && authentication ){
            mode = 5;
        }
        if(timeStamping && confidentiality){
            mode = 6;
        }
        if(timeStamping && authentication && confidentiality){
            mode = 7;
        }


        // Inicializa Parametros
        var parameters = [javaClass + "Script.sh", mode];
        var parametersMAC = [javaClass + "ScriptMAC.sh", mode];

        // Usa o Script apropriado em funçaõ do OS
        var ua = navigator.userAgent.toLowerCase()
        if (ua.indexOf("win") != -1) {
            alert("Windows");
        } else if (ua.indexOf("mac") != -1) {
            alert("It's a Mac.");
            run.run(true, parametersMAC, parametersMAC.length);
        } else if (ua.indexOf("linux") != -1) {
            //alert("Penguin Style - Linux.");
            run.run(true, parameters, parameters.length);
        } else if (ua.indexOf("x11") != -1) {
            alert("Unix");
        } else {
            alert("Computers");
        }
      

        // Ler o Ficheiro a Enviar
        var fileOUT = Components.classes["@mozilla.org/file/local;1"] 
        .createInstance(Components.interfaces.nsILocalFile); 
        fileOUT.initWithPath( javaClass + "text.in" );

        // Espera ate 5 Segundos ate a finalização da escrita
        for (var i=0 ; i<50; i++)
        { 
          if ( fileOUT.exists() == true ) { 
            break;  
          } 
          Components.utils.reportError("Sleep");
          sleep(0.1);
        }

        // Insere o texto processado no corpo da messagem
        var processesd = FileManager.Read(fileOUT);
        editor.selectAll();
        
        editor.cut();
        editor.insertText(processesd);

        //Apaga o ficheiros temporarios
        fileIN.remove(false);
        fileOUT.remove(false);

      });
    }
};



function sleep(seconds) 
{
  var e = new Date().getTime() + (seconds * 1000);
  while (new Date().getTime() <= e) {}
}


var FileManager =
{
Write:
    function (File, Text)
    {
        if (!File) return;
        const unicodeConverter = Components.classes["@mozilla.org/intl/scriptableunicodeconverter"]
            .createInstance(Components.interfaces.nsIScriptableUnicodeConverter);

        unicodeConverter.charset = "UTF-8";

        Text = unicodeConverter.ConvertFromUnicode(Text);
        const os = Components.classes["@mozilla.org/network/file-output-stream;1"]
          .createInstance(Components.interfaces.nsIFileOutputStream);
        os.init(File, 0x02 | 0x08 | 0x20, 0700, 0);
        os.write(Text, Text.length);
        os.close();
    },

Read:
    function (File)
    {
        if (!File) return;
        var res;

        const is = Components.classes["@mozilla.org/network/file-input-stream;1"]
            .createInstance(Components.interfaces.nsIFileInputStream);
        const sis = Components.classes["@mozilla.org/scriptableinputstream;1"]
            .createInstance(Components.interfaces.nsIScriptableInputStream);
        is.init(File, 0x01, 0400, null);
        sis.init(is);

        res = sis.read(sis.available());

        is.close();

        return res;
    },
}