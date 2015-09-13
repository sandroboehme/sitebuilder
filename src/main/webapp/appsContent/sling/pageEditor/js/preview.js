var iFrameContent = null;

makeContainerSortable();

		function postJSPChange(operation, id, referenceElementId, content){
			// remove the content to make the post smaller if it's an 'add' operation
			content = (typeof operation != "undefined" && operation != null && operation.startsWith("order")) ? "" : content;
			var url = $('#page-preview').attr("data-jsp");
			$.ajax({
				type: "POST",
				success: function( data ) {
				},
				data: { 
					":operation": "modify-jsp",
					":modifyServletOperation": operation,
					"id": id,
					"referenceElementId": referenceElementId,
					"content": content,
					},
				dataType: "text",
				url: url
				});
		}
		
		function addId(element){
	    	var lastId = $("#page-preview").attr("data-last-id");
	    	if (typeof lastId == "undefined" || lastId == null) {
	    		lastId = 0;
	    		$('.component,.component-container').each(function(index){
	    			var currIdString = $(this).attr("id");
	    			var currId = (typeof currIdString != "undefined" && currIdString != null) ? parseInt(currIdString.substr(4)) : 0;
	    			lastId = currId > lastId ? currId : lastId ; 
	    		});
	    	}
	    	lastId++;
	    	$(element).attr("id", "cms_"+lastId);
	    	$("#page-preview").attr("data-last-id",lastId);
		}
		
		function makeContainerSortable(){
			// order of events: 1. receive, 2. sortable stop, 3. draggable stop
			// see: http://jsfiddle.net/vdun28bj/31/
			// http://stackoverflow.com/questions/31613338/jquery-ui-sortable-cancel-option-for-multiple-nested-levels
			// http://jericho.htmlparser.net/docs/index.html
//	            $( "#page-preview .component-container" ).sortable({
//				connectWith: "#page-preview .component-container",
//			    cancel: "#page-preview .component-container > :not(.component,.component-container)",
//				handle: ".drag",
//				tolerance: "pointer",
//			    stop: function (event, ui) {
//			    	alert("dropping!");
//					var operation = "";
//					var referenceElementId = -1;
//					
//			    	var addedComponent = typeof ui.item != "undefined" && ui.item != null && ui.item.data("addedComponent");
//
//			    	if (addedComponent) {
//			    		addId(ui.item);
//			    		ui.item.find(".component-container").each(function( index ) {
//			    			addId($( this ));
//			    		});
//			    		// removes the fixed width and height that the draggable has set
//				    	$(ui.item).removeAttr("style");
//			    	}
//			    	
//			    	var next = ui.item.next();
//			    	if (typeof next != "undefined" && next != null && next.hasClass("component")) {
//			    		// add/move before next
//			    		operation = addedComponent ? "addbefore" : "orderbefore";
//			    		referenceElementId = next.attr("id");
//			    	} else {
//			    		var prev = ui.item.prev();
//				    	if (typeof prev != "undefined" && prev != null && prev.hasClass("component")) {
//				    		// add/move after prev
//				    		operation = addedComponent ? "addafter" : "orderafter";
//				    		referenceElementId = prev.attr("id");
//				    	} else {
//				    		var noPrev = typeof prev == "undefined" || prev == null || prev.length == 0;
//				    		var parent = ui.item.parent();
//					    	if (parent.hasClass("component-container") && noPrev) {
//					    		// add/move at first position within parent
//					    		operation = addedComponent ? "addwithinfirst" : "orderwithinfirst";
//					    		referenceElementId = parent.attr("id");
//					    	} else {
//					    		// no referencable component with an id found ==> cancel
//					    		$(this).sortable("cancel");
//						    	if (addedComponent) {
//						    		// cancel doesn't work on added components (draggables)
//						    		ui.item.remove()
//						    	}
//					    	}
//				    	}
//			    	}
//
//					postJSPChange(operation, ui.item.attr("id"), referenceElementId, ui.item[0].outerHTML);
//		    	}
//		    });
		}
		
//		$( document ).ready(function() {
				$('.delete').on("click", function(){
					var component = $(this).parents(".component:first");
					var idToDelete = component.attr("id");
					postJSPChange("delete", idToDelete);
					component.remove();
				})
				
				$("body .component-container > :not(.component,.component-container)").css("color","red");
				
//				order of events: 1. receive, 2. sortable stop, 3. draggable stop
				makeContainerSortable();
	            
			/*
			 */
//		});