//Components.utils.import("resource://aesecure/modules/base64.js");
Components.utils.import("resource://gre/modules/AddonManager.jsm");

var authentication = false;
var confidentiality = true;

var balanuta = {

  LiveConnect : {},
    run : function(){
      //Components.utils['import']('resource://aesecure/LiveConnectUtils.js', this.LiveConnect);
      //  var jars = ['URLSetPolicy.jar', 'Test.jar'];
      //  var [loader,urls] = this.LiveConnect.initWithPrivs(java, 'artur.balanuta@ist.utl.pt', jars);

//        var aClass = java.lang.Class.forName('Test', true, loader);
  //      var aStaticMethod = aClass.getMethod('getGreetings', []); // Fails here
    //    var greeting = aStaticMethod.invoke(null, []);
      //  alert(greeting);

        /*Code*/

        //var file = Components.classes["@mozilla.org/file/local;1"].
        //createInstance(Components.interfaces.nsILocalFile);
        //file.initWithPath("/usr/bin/firefox");
        //file.launch();

      var editor = GetCurrentEditor();  
      var editor_type = GetCurrentEditorType();  
      //editor.beginTransaction();
      var texto = editor.outputToString('text/plain', 4);
      editor.selectAll();
      editor.cut();
      //editor.insertText("OMG cenas");
      //editor.endTransaction();

        //netscape.
        //var exe = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);
        //exe.initWithPath("/");
        //exe.append("bin");
        //exe.append("sh");
        //var run = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
       //run.init(exe);        
        //var parameters = ["-cv", '/usr/bin/gedit'];
        //run.run(true, parameters, parameters.length);

        // read encriptedls


      AddonManager.getAddonByID("artur.balanuta@ist.utl.pt", function(addon) {
                        var addonLocation = addon.getResourceURI("").QueryInterface(Components.interfaces.nsIFileURL).file.path;
                        var javaClass = addonLocation + "/chrome/content/java/";

                        var exe = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);
                        exe.initWithPath("/");
                        exe.append("bin");
                        exe.append("sh");
                        
                        var run = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
                        run.init(exe);       

                        var parameters = [javaClass + "Script.sh"];

                        run.run(true, parameters, parameters.length);
                        alert("-cp " + javaClass + "Script.sh", texto);
                      });

       //throw new Error("Text: " + texto);
      //throw new Error("Auth:" + authentication  + " Conf:" +  confidentiality);
    }
};