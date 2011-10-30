<%@ include file="/include.jsp" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<script type="text/javascript">
    function sendTest() {

        var apiKey = $('properties[apiKey].value');
        if(!apiKey || apiKey.value.length == 0) {
            alert("Please enter the Prowl API key!");
            return;
        }

        var pm = $('prowlTestMessage').value;
        if(!pm || pm.length ==0) {
            return;
        }

        BS.ajaxRequest($('prowlTestForm').action, {
            parameters: 'prowlApiKey='+ apiKey.value + '&prowlTestMessage='+pm,
            onComplete: function(transport) {
              if (transport.responseXML) {
                  $('tcProwlTest').refresh();
              }
            }
        });
        return false;
    }
</script>

<bs:refreshable containerId="tcProwlTest" pageUrl="${pageUrl}">
<bs:messages key="tcprowlMessage"/>

<form action="/tcprowlSettings.html" method="post" id="prowlTestForm">
Send test message to Prowl server: <input id="prowlTestMessage" name="prowlTestMessage" type="text" />  <input type="button" name="Test" value="Test" onclick="return sendTest();"/>
</form>
</bs:refreshable>
