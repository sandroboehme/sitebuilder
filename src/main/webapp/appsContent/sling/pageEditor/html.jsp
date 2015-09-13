
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<sling:defineObjects />

<%-- see: http://stackoverflow.com/questions/16731203/invalid-mouse-offset-for-cross-frame-draggable-sortable/31999152#31999152  --%>
<iframe id="iframe_main" src="/pageeditor${resource.path}.main.html" class="" allowtransparency="true" frameborder="0" height="100%" width="100%" style="height: 100%;"></iframe>
