"use strict";
use(function () {
    var returnObj = {
    		resource: resource
    };
    log.error("request.getRequestURI(): "+request.getRequestURI());
    log.error("this.href: "+this.href);
    var prefixWithoutExtension = removeExtension(this.href);
    return startsWith(request.getRequestURI()+"", prefixWithoutExtension);
    
    function startsWith(string, prefix) {
        return prefix.length <= string.length && string.substring(0, prefix.length) == prefix;
    };
    
    function removeExtension(uri){
    	return uri.substring(0, uri.lastIndexOf("."));
    }
    
});