Components.utils.import("resource:///modules/gloda/index_msg.js");
Components.utils.import("resource:///modules/gloda/mimemsg.js");
// This is Thunderbird 3.3 only!
Components.utils.import("resource://gre/modules/Services.jsm");
Components.utils.import("resource://gre/modules/NetUtil.jsm");

var Glodebug = {
  do: function () {
    // First we get msgHdr, a nsIMsgDbHdr
    let msgHdr = gFolderDisplay.selectedMessage;
    let text = [];
    let add = function (s) text.push(s);
    let count = 3;
    let top = function () {
      if (--count == 0)
        document.getElementById("tabmail").openTab("contentTab", {
          contentPage: "about:blank",
          onLoad: function (aEvent, aBrowser) {
            let doc = aBrowser.contentDocument;
            let pre = doc.createElement("pre");
            pre.textContent = text.join("");
            doc.body.appendChild(pre);
          },
        });
    }
    // The first way of examining it is through the MimeMessage representation
    MsgHdrToMimeMessage(msgHdr, null, function (aMsgHdr, aMimeMsg) {
      add("\t\t\t/---------------------------\\\n");
      add("\t\t\t|       Mime  results       |\n");
      add("\t\t\t\\---------------------------/\n\n");
      add("Size of the message: "+aMimeMsg.size+"\n");
      add("Structure of the message:\n"+aMimeMsg.prettyString()+"\n");
      // allUserAttachments is Thunderbird 3.3 only. See bug 576282 about why
      // this is probably what you want, rather than allAttachments.
      let attachments = aMimeMsg.allUserAttachments || aMimeMsg.allAttachments;
      add("Number of attachments: "+attachments.length+"\n");
      add("This message is from: "+aMimeMsg.headers["from"]+"\n");
      add("List? "+aMimeMsg.headers["list-post"]+"\n");
      // For example, let's just say we want to get the first attachment's raw
      // contents.
      if (false && attachments.length) {
        count++;
        let att = attachments[0];
        let url = Services.io.newURI(att.url, null, null);
        let channel = Services.io.newChannelFromURI(url);
        let chunks = [];
        let unicodeConverter = Cc["@mozilla.org/intl/scriptableunicodeconverter"]
                               .createInstance(Ci.nsIScriptableUnicodeConverter);
        unicodeConverter.charset = "UTF-8";
        let listener = {
          setMimeHeaders: function () {
          },

          onStartRequest: function (/* nsIRequest */ aRequest, /* nsISupports */ aContext) {
          },

          onStopRequest: function (/* nsIRequest */ aRequest, /* nsISupports */ aContext, /* int */ aStatusCode) {
            let data = chunks.join("");
            add("Raw contents for the first attachment: \n"+data+"\n");
            top();
          },

          onDataAvailable: function (/* nsIRequest */ aRequest, /* nsISupports */ aContext,
              /* nsIInputStream */ aStream, /* int */ aOffset, /* int */ aCount) {
            // Fortunately, we have in Gecko 2.0 a nice wrapper
            let data = NetUtil.readInputStreamToString(aStream, aCount);
            // Now each character of the string is actually to be understood as a byte
            //  of a UTF-8 string.
            // Everyone knows that nsICharsetConverterManager and nsIUnicodeDecoder
            //  are not to be used from scriptable code, right? And the error you'll
            //  get if you try to do so is really meaningful, and that you'll have no
            //  trouble figuring out where the error comes from...
            // So charCodeAt is what we want here...
            let array = [];
            for (let i = 0; i < data.length; ++i)
              array[i] = data.charCodeAt(i);
            // Yay, good to go!
            chunks.push(unicodeConverter.convertFromByteArray(array, array.length));
          },

          QueryInterface: XPCOMUtils.generateQI([Ci.nsISupports, Ci.nsIStreamListener,
            Ci.nsIMsgQuotingOutputStreamListener, Ci.nsIRequestObserver])
        };
        channel.asyncOpen(listener, null);
      }
      add("\n");
      top();
      // Do more stuff...
    });
    // If you don't want to stream the whole message (slow) and don't mind
    // depending on Gloda, you can run a Gloda query.
    this.query =
      Gloda.getMessageCollectionForHeaders([msgHdr], {
        onItemsAdded: function (aItems) {},
        onItemsModified: function () {},
        onItemsRemoved: function () {},
        onQueryCompleted: function (aCollection) {
          add("\t\t\t/---------------------------\\\n");
          add("\t\t\t|       Gloda results       |\n");
          add("\t\t\t\\---------------------------/\n\n");
          // If zero messages are found, this means either the message hasn't
          // been indexed yet, or the Gloda is disabled for that folder /
          // profile. Use the MimeMessage method to get the information you
          // need!
          add("Gloda found "+aCollection.items.length+" items\n");
          // Iterator over the messages Gloda found...
          for each (let [i, glodaMsg] in Iterator(aCollection.items)) {
            add("This message is from: "+glodaMsg.from+"\n");
            add("This message is to: "+glodaMsg.to+"\n");
            add("This message is from lists: "+glodaMsg.mailingLists+"\n");
            // Thunderbird 3.3
            if ("attachmentInfos" in glodaMsg) {
              let attachments = glodaMsg.attachmentInfos;
              add("This message has "+attachments.length+" attachments\n");
              for each (let [i, att] in Iterator(attachments)) {
                add("Attachment "+i+" is "+att+"\n");
                for each (let k in ["contentType", "size", "url", "name"]) {
                  add("Attachment "+i+" key="+k+" value="+att[k]+"\n");
                }
              }
            }
          }
          top();
          // There's plenty of other stuff you can, e.g.
          //   glodaMsg.conversation.getMessagesCollection(...);
          // See the Gloda Examples on MDC.
        },
      }, true)
    ;
    top();
    // This just kicks a reindexing in, so that if you are writing a Gloda
    // plugin, you can see the reindexing happen...
    GlodaMsgIndexer.indexMessages([
      [x.folder, x.messageKey]
      for each ([, x] in Iterator(gFolderDisplay.selectedMessages))
    ]);
  }
}
