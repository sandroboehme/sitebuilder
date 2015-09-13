var iFrameContent = null;

		function getScriptContainerStack(component){
			var closestScriptName = "";
			var idStack = [];
			var scriptComponentStack = component.parents('.script-container');
			scriptComponentStack.each(function(){
				idStack.unshift($(this).attr("data-component-id"));
			});
			return idStack.join("_");
		}
		
		function postJSPChange(operation, componentId, scriptIdStack, referenceElementId, referenceScriptIdStack, sortable, idComponent, lastId){
			var thisIdComponent = idComponent;
			$.ajax({
				type: "POST",
				success: function( data ) {
					thisIdComponent.attr("data-component-id",data.newComponentId);
					makeComponentsEditable();
				},
				data: { 
					":operation": "modify-jsp",
					":modifyServletOperation": operation,
					"componentId": componentId,
					"referenceElementId": referenceElementId,
					"scriptIdStack": scriptIdStack,
					"referenceScriptIdStack": referenceScriptIdStack,
					"lastId": lastId
					},
				dataType: "json",
				url: $('#iframe_editor').get(0).contentWindow.location.href
			}).fail(function( jqXHR, textStatus, errorThrown ) {
				if (typeof sortable != "undefined" && sortable != null) {
					sortable.sortable("cancel");
				}
				alert(jqXHR.responseJSON["status.message"]);
			});
		}
		
		function refreshIframeHeight(){
            var iframeHeight = document.getElementById('iframe_editor').contentWindow.document.body.offsetHeight;
            $('#iframe_editor').height(iframeHeight);
		}
		
		function makeComponentsEditable(){
			$('#iframe_editor').contents().find('.edit').each(function(){
				var href = $(this).parents(".script-container:first").attr('data-resource-path');
				var componentWrapper = $(this).parents(".component-wrapper:first");
				var component = componentWrapper.find('.component:first');
//				var componentNodeName = getScriptContainerStack(component)+"_"+component.attr("data-component-id");
				var componentNodeName = component.attr("data-component-id");
				href = "/reseditor"+href+"/"+componentNodeName+".html";
				$(this).attr("href", href);
			})
		}
		
		function makeComponentsDeletable(){
			$('#iframe_editor').contents().find('.delete').on("click", function(){
				var componentWrapper = $(this).parents(".component-wrapper:first");
				var component = componentWrapper.find('.component');
				var idToDelete = component.attr('data-component-id')
				var idStack = getScriptContainerStack(component);
				postJSPChange("delete", idToDelete, idStack);
				componentWrapper.remove();
			})
		}
		
		function serverSideEnrichment() {
			var scriptStacks = [];
			$('#iframe_editor').contents().find('.script-container').each(function(){
				var parentStack = getScriptContainerStack($(this));
				if ("" === parentStack) { // root script container
					scriptStacks.push($(this).attr("data-component-id"));
				} else {
					scriptStacks.push(parentStack+"_"+$(this).attr("data-component-id"));
				}
			});
			var scriptStacksParameter = scriptStacks.join("&scriptIdStack=");
			var href = $('#iframe_editor').get(0).contentWindow.location.href+".scriptcontainer?scriptIdStack="+scriptStacksParameter;
			$.getJSON(href, function(data){
				$(data).each(function(){
					var resPath = this.resolvedScriptStack.resourcePath;
					var scriptPath = this.resolvedScriptStack.scriptPath;
					var scriptContainer = getScriptContainer(this.scriptStack);

				   scriptContainer.attr("data-resource-path",resPath);
				   scriptContainer.attr("data-script-path", scriptPath);
				   var toolbar = scriptContainer.prev(".component-toolbar");
				   toolbar.attr("title", resPath+"=>"+scriptPath);
				});
				makeComponentsEditable();
			});
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
			$('#iframe_editor').contents().find(cssSelector).each(function(){
				   if($(this).parents('.script-container').size() == ids.length-1) {
					   scriptContainer = $(this);
				   }
			});
			return scriptContainer;
		}
		
		function getLastId(referenceComponent, idComponent){
			var lastId = 0;
			var includeParentSelector = '.script-container[data-component-type=\"sling-include\"]';
			var hasIncludeParent = referenceComponent.parents(includeParentSelector+":first").length==1;
			referenceComponent.parents(hasIncludeParent ? includeParentSelector+":last" : "body")
				.find('.component, .component-container, .script-container')
				.filter(function(index, currentElement){
					var includeParent = hasIncludeParent ? 1 : 0;
					return $(currentElement).parents(includeParentSelector).length == includeParent;
				})
				.each(function(){
					if (this != idComponent[0]){
						var currentIdString = $(this).attr("data-component-id");
						var currentId = Number(currentIdString);
						if (!isNaN(currentId)){
							lastId = lastId < currentId ? currentId : lastId;
						}
					}
				});
			return lastId;
		}
		
		function makeContainerSortable(){
			var scriptIdStack = "";
			// order of events: 1. receive, 2. sortable stop, 3. draggable stop
			// see: http://jsfiddle.net/vdun28bj/31/
			// http://stackoverflow.com/questions/31613338/jquery-ui-sortable-cancel-option-for-multiple-nested-levels
			// http://jericho.htmlparser.net/docs/index.html
	        var contents = $('#iframe_editor').contents();
		    contents.find( "#page-preview .component-container" ).sortable({
				connectWith: $('#iframe_editor').contents().find("#page-preview .component-container"),
				handle: ".drag",
			    cancel: $('#iframe_editor').contents().find("#page-preview .component-container > :not(.component,.component-container,.component-wrapper)"),
				tolerance: "pointer",
				cursorAt: { top: 0, left: 0 },
				start: function (event, ui) {
					scriptIdStack = getScriptContainerStack(ui.item.find(".component:first"));
					$(ui.item).css("width", "");
					$(ui.item).css("height", "");
				},
			    stop: function (event, ui) {
					var operation = "";
					var referenceElementId = -1;
					var referenceComponent = null
					
			    	var addedComponent = typeof ui.item != "undefined" && ui.item != null && ui.item.data("addedComponent");

			    	if (addedComponent) {
//			    		addId(ui.item.find(".component:first"));
//			    		ui.item.find(".component-container").each(function( index ) {
//			    			addId($( this ));
//			    		});
			    		// removes the fixed width and height that the draggable has set
				    	$(ui.item).removeAttr("style");
			    	}
			    	
			    	var next = ui.item.next();
			    	if (typeof next != "undefined" && next != null && next.hasClass("component-wrapper")) {
			    		// add/move before next
			    		operation = addedComponent ? "addbefore" : "orderbefore";
			    		referenceComponent = next.find(".component:first");
			    		referenceElementId = referenceComponent.attr("data-component-id");
			    	} else {
			    		var prev = ui.item.prev();
				    	if (typeof prev != "undefined" && prev != null && prev.hasClass("component-wrapper")) {
				    		// add/move after prev
				    		operation = addedComponent ? "addafter" : "orderafter";
				    		referenceComponent = prev.find(".component:first");
				    		referenceElementId = referenceComponent.attr("data-component-id");
				    	} else {
				    		var noPrev = typeof prev == "undefined" || prev == null || prev.length == 0;
				    		var parent = ui.item.parent();
					    	if (parent.hasClass("component-container") && noPrev) {
					    		// add/move at first position within parent
					    		operation = addedComponent ? "addwithinfirst" : "orderwithinfirst";
					    		referenceComponent = parent;
					    		referenceElementId = referenceComponent.attr("data-component-id");
					    	} else {
					    		// no referencable component with an id found ==> cancel
					    		$(this).sortable("cancel");
						    	if (addedComponent) {
						    		// cancel doesn't work on added components (draggables)
						    		ui.item.remove()
						    	}
					    	}
				    	}
			    	}

					var referenceScriptIdStack = getScriptContainerStack(referenceComponent);
					var idComponent = ui.item.find(".component:first");
					var lastId = getLastId(referenceComponent, idComponent);
					postJSPChange(operation, idComponent.attr("data-component-id"), scriptIdStack, referenceElementId, referenceScriptIdStack, $(this), idComponent, lastId);
		    	}
		    });
		}
		
		
		$( document ).ready(function() {
			$('#iframe_editor').load(function () {
				iFrameContent = $('#iframe_editor').contents();

			    if (iFrameContent.find("head") != undefined) {
			    	iFrameContent.find("head").append('<link rel="stylesheet" href="'+contextPath+'/apps/sling/pageEditor/css/preview.css" type="text/css" />');
			    }
			    
	            $(".component-label").draggable({
	                appendTo: 'body',
					connectToSortable: iFrameContent.find(".component-container"),
					helper: "clone",
					handle: ".drag",
					tolerance: "pointer",
					iframeFix: true,
			        cursorAt: { top: 0, left: 0 },
					start: function( event, ui ) {
						var componentType = $(this).attr("data-component-type");
						var newComponent = $("#"+componentType+" .component").clone();
						ui.helper.empty();
						ui.helper.append($('#prototypes .component-wrapper .component-toolbar').clone())
						ui.helper.append(newComponent);
						ui.helper.removeClass();
						ui.helper.addClass("component-wrapper");
						ui.helper.data("addedComponent", true);
					},
					stop: function(event, ui){
						makeContainerSortable();
						makeComponentsDeletable();
						serverSideEnrichment();
			    		ui.helper.removeData("addedComponent");
					}
				});

				
				$('#iframe_editor').contents().find("#page-preview .component-container > :not(.component,.component-container,.component-wrapper)").css("color","red");
				
				$('#iframe_editor').contents().find('.component').wrap($('#prototypes .component-wrapper'));
				
				$('#iframe_editor').contents().find('.component-wrapper').each(function(index, object){
					var component = $(this).find('.component:first');
					var toolbar = $(this).find('.component-toolbar:first');
					var compType = component.attr('data-component-type'); 
					toolbar.prepend("<span class='component-type'>"+compType+"</span>");
				});
				
	            refreshIframeHeight();

//				order of events: 1. receive, 2. sortable stop, 3. draggable stop
				
				makeContainerSortable();
				makeComponentsDeletable();
				serverSideEnrichment();
				
				$('[data-toggle="tooltip"]').tooltip();
			});
		});