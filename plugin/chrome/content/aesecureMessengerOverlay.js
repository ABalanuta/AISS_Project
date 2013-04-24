Components.utils.import("resource://gre/modules/AddonManager.jsm");




var AESecure = {

  LiveConnect : {},
    
    read : function(){

        //var button = document.getElementById("button-aesecure-decrypt");
        //button.setAttribute("disabled", "true");


        //  WORK




        AddonManager.getAddonByID("artur.balanuta@ist.utl.pt", function(addon) {
        var addonLocation = new String(addon.getResourceURI("").QueryInterface(Components.interfaces.nsIFileURL).file.path);

        var javaClass = addonLocation + "/chrome/content/java/";
        //Components.utils.reportError("JavaFolder: "+ javaClass);

        var exe = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);
        exe.initWithPath("/");
        exe.append("bin");
        exe.append("sh");
                          
      //  var run = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
//        run.init(exe);       

       // var parameters = [javaClass + "Script.sh", encoded, authentication, confidentiality, '"'+javaClass+'"'];
        //Components.utils.reportError("parametersALL: "+parameters);
      //  run.run(true, parameters, parameters.length);
        //alert("-cp " + javaClass + "Script.sh", '"' + texto + '"');
      


        // Ler o Ficheiro Criado
        var file = Components.classes["@mozilla.org/file/local;1"] 
        .createInstance(Components.interfaces.nsILocalFile); 
        file.initWithPath( javaClass + "text.in" );
        var text = FileManager.Read(file);


        var currentReadMessage = document.getElementById("messagepane").contentDocument.body.textContent;
        //var currentReadMessage = document.getElementById("messagepane").contentDocument.body.innerHTML;
        //Components.utils.reportError("Message is: "+ currentReadMessage);
        document.getElementById("messagepane").contentDocument.body.innerHTML = text;

        // Espera por 20 Segundos ate a finalização da escrita
  //      for (var i=0 ; i<20; i++)
    //    { 
     //     if ( file.exists() == true ) { 
      //      break;  
       //   } 
        //  Components.utils.reportError("Sleep");
        //  sleep(1);
       // }

       // var processesd = FileManager.Read(file);

//        editor.cut();
 //       editor.insertText(processesd);

        //Apaga o ficheiro temporario
   //     file.remove(false);

        


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



// to Base64 Functions

/*
Copyright (c) 2008 Fred Palmer fred.palmer_at_gmail.com

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/
function StringBuffer()
{ 
    this.buffer = []; 
} 

StringBuffer.prototype.append = function append(string)
{ 
    this.buffer.push(string); 
    return this; 
}; 

StringBuffer.prototype.toString = function toString()
{ 
    return this.buffer.join(""); 
}; 

var Base64 =
{
    codex : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    encode : function (input)
    {
        var output = new StringBuffer();

        var enumerator = new Utf8EncodeEnumerator(input);
        while (enumerator.moveNext())
        {
            var chr1 = enumerator.current;

            enumerator.moveNext();
            var chr2 = enumerator.current;

            enumerator.moveNext();
            var chr3 = enumerator.current;

            var enc1 = chr1 >> 2;
            var enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            var enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            var enc4 = chr3 & 63;

            if (isNaN(chr2))
            {
                enc3 = enc4 = 64;
            }
            else if (isNaN(chr3))
            {
                enc4 = 64;
            }

            output.append(this.codex.charAt(enc1) + this.codex.charAt(enc2) + this.codex.charAt(enc3) + this.codex.charAt(enc4));
        }

        return output.toString();
    },

    decode : function (input)
    {
        var output = new StringBuffer();

        var enumerator = new Base64DecodeEnumerator(input);
        while (enumerator.moveNext())
        {
            var charCode = enumerator.current;

            if (charCode < 128)
                output.append(String.fromCharCode(charCode));
            else if ((charCode > 191) && (charCode < 224))
            {
                enumerator.moveNext();
                var charCode2 = enumerator.current;

                output.append(String.fromCharCode(((charCode & 31) << 6) | (charCode2 & 63)));
            }
            else
            {
                enumerator.moveNext();
                var charCode2 = enumerator.current;

                enumerator.moveNext();
                var charCode3 = enumerator.current;

                output.append(String.fromCharCode(((charCode & 15) << 12) | ((charCode2 & 63) << 6) | (charCode3 & 63)));
            }
        }

        return output.toString();
    }
}


function Utf8EncodeEnumerator(input)
{
    this._input = input;
    this._index = -1;
    this._buffer = [];
}

Utf8EncodeEnumerator.prototype =
{
    current: Number.NaN,

    moveNext: function()
    {
        if (this._buffer.length > 0)
        {
            this.current = this._buffer.shift();
            return true;
        }
        else if (this._index >= (this._input.length - 1))
        {
            this.current = Number.NaN;
            return false;
        }
        else
        {
            var charCode = this._input.charCodeAt(++this._index);

            // "\r\n" -> "\n"
            //
            if ((charCode == 13) && (this._input.charCodeAt(this._index + 1) == 10))
            {
                charCode = 10;
                this._index += 2;
            }

            if (charCode < 128)
            {
                this.current = charCode;
            }
            else if ((charCode > 127) && (charCode < 2048))
            {
                this.current = (charCode >> 6) | 192;
                this._buffer.push((charCode & 63) | 128);
            }
            else
            {
                this.current = (charCode >> 12) | 224;
                this._buffer.push(((charCode >> 6) & 63) | 128);
                this._buffer.push((charCode & 63) | 128);
            }

            return true;
        }
    }
}

function Base64DecodeEnumerator(input)
{
    this._input = input;
    this._index = -1;
    this._buffer = [];
}

Base64DecodeEnumerator.prototype =
{
    current: 64,

    moveNext: function()
    {
        if (this._buffer.length > 0)
        {
            this.current = this._buffer.shift();
            return true;
        }
        else if (this._index >= (this._input.length - 1))
        {
            this.current = 64;
            return false;
        }
        else
        {
            var enc1 = Base64.codex.indexOf(this._input.charAt(++this._index));
            var enc2 = Base64.codex.indexOf(this._input.charAt(++this._index));
            var enc3 = Base64.codex.indexOf(this._input.charAt(++this._index));
            var enc4 = Base64.codex.indexOf(this._input.charAt(++this._index));

            var chr1 = (enc1 << 2) | (enc2 >> 4);
            var chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            var chr3 = ((enc3 & 3) << 6) | enc4;

            this.current = chr1;

            if (enc3 != 64)
                this._buffer.push(chr2);

            if (enc4 != 64)
                this._buffer.push(chr3);

            return true;
        }
    }
};
