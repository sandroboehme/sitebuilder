
		var org = org || {};
		org.sling = org.sling || {};
		org.sling.sitebuilder = org.sling.sitebuilder || {};
		org.sling.sitebuilder.componentScripts = {};
		
		org.sling.sitebuilder.Client = (function() {
			function Client(){
				serverSideEnrichment();
			
				function getScriptContainerStack(component){
					var closestScriptName = "";
					var idStack = [];
					var scriptComponentStack = component.parents('.script-container');
					scriptComponentStack.each(function(){
						idStack.unshift($(this).attr("data-component-id"));
					});
					return idStack.join("_");
				}
				
				function serverSideEnrichment() {
					var scriptStacks = [];
					$('.script-container').each(function(){
						if ($(this).parents(".component-stock").length == 0) {
							var parentStack = getScriptContainerStack($(this));
							if ("" === parentStack) { // root script container
								scriptStacks.push($(this).attr("data-component-id"));
							} else {
								scriptStacks.push(parentStack+"_"+$(this).attr("data-component-id"));
							}
						}
					});
					if (scriptStacks.length>0){ //not needed e.g. in the component-stack
						var scriptStacksParameter = scriptStacks.join("&scriptIdStack=");
						var host = location.host;
						var pathname = location.pathname;
						if (pathname.startsWith("/pageeditor")){
							pathname = pathname.substring("/pageeditor".length);
						}
						var href = "http://"+host+pathname+".scriptcontainer?scriptIdStack="+scriptStacksParameter;
						$.getJSON(href, function(data){
							$(data).each(function(){
								var resPath = this.resolvedScriptStack.resourcePath;
								var scriptContainer = getScriptContainer(this.scriptStack);
			
							   scriptContainer.attr("data-resource-path",resPath);
							});
							callComponentScripts();
						});
					}
				}
				
				function getScriptContainer(scriptStack){
					var cssSelector = "";
					var ids = scriptStack.split("_");
					// Check from time to time if there is a better solution than the currently 
					// implemented one.
					// http://stackoverflow.com/questions/32446692/css-select-based-on-logical-hierarchy/32467251#32467251
					$(ids).each(function(){
						cssSelector+=".script-container[data-component-id='"+this+"'] ";
					});
					var scriptContainer = null;
					$(cssSelector).each(function(){
						   if($(this).parents('.script-container').size() == ids.length-1) {
							   scriptContainer = $(this);
						   }
					});
					return scriptContainer;
				}
			}
			
			function callComponentScripts(){
				for (var index in org.sling.sitebuilder.componentScripts){
					new org.sling.sitebuilder.componentScripts[index]();
				}
			}

			
			Client.prototype.getComponentJSON = function(component, callback) {
				var url = $(component).parents(".script-container:first").attr('data-resource-path');
				url= url+"/"+$(component).attr('data-component-id');
				url= url+".json";
				var thisComponent = this;
				$.getJSON( url, function( data ) {
					callback(data);
				});
				
			}
		return Client;
		}());

		$( document ).ready(function() {
			org.sling.sitebuilder.client = new org.sling.sitebuilder.Client();
		});