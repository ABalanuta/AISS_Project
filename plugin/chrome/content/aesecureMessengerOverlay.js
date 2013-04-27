Components.utils.import("resource://gre/modules/AddonManager.jsm");




var AESecure = {

  LiveConnect : {},

  decrypt : function(){

        //var button = document.getElementById("button-aesecure-decrypt");
        //button.setAttribute("disabled", "true");

        var texto = document.getElementById("messagepane").contentDocument.body.textContent;
        //texto = texto.replace( /[\n]+/g, '' );
        texto = texto.substring(21);



        AddonManager.getAddonByID("artur.balanuta@ist.utl.pt", function(addon) {
            var addonLocation = new String(addon.getResourceURI("").QueryInterface(Components.interfaces.nsIFileURL).file.path);
            var javaClass = addonLocation + "/chrome/content/java/";

            // Grava a messagem para um ficheiro de texto
            var fileIN = Components.classes["@mozilla.org/file/local;1"] 
            .createInstance(Components.interfaces.nsILocalFile); 
            fileIN.initWithPath( javaClass + "text.in" );
            FileManager.Write(fileIN, texto);


            var exe = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);
            exe.initWithPath("/");
            exe.append("bin");
            exe.append("sh");

            var run = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
            run.init(exe);       

            var mode = 0;   //Descifra

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
            
            // Ler o Ficheiro a Processado
            var fileOUT = Components.classes["@mozilla.org/file/local;1"] 
            .createInstance(Components.interfaces.nsILocalFile); 
            fileOUT.initWithPath(javaClass + "in/" + "text.in");


            // Espera ate 5 Segundos ate a finalização da escrita
            for (var i=0 ; i<50; i++)
            { 
              if ( fileOUT.exists() == true ) { 
                break;  
            }
                sleep(0.1);
            }

            // Insere o texto processado no corpo da messagem
            var processesd = FileManager.Read(fileOUT);
            //document.getElementById("messagepane").contentDocument.body.innerHTML = processesd;

             // Ler o Ficheiro a Processado
            var fileLOG = Components.classes["@mozilla.org/file/local;1"] 
            .createInstance(Components.interfaces.nsILocalFile); 
            fileLOG.initWithPath(javaClass + "validationLog.txt");
            var log = FileManager.Read(fileLOG);
            //processesd = processesd.substring(0, processesd.length -19) + "##" + "</body></html>"
            //processed.replace(/\\n/g, "<br/>");
            document.getElementById("messagepane").contentDocument.body.innerHTML = processesd + '<br>';
            document.getElementById("messagepane").contentDocument.body.innerHTML += log;

            //document.getElementById("messagepane").contentDocument.body.textContent = 

            //Apaga o ficheiros temporarios
             //fileIN.remove(false);
             //fileOUT.remove(false);
             //fileLOG.remove(false);

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
