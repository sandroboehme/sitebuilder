<!DOCTYPE html>

<%@ page session="false"%>
<%@ page isELIgnored="false"%>
<%@ page import="javax.jcr.*,org.apache.sling.api.resource.Resource"%>
<%@ page import="java.security.Principal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="sb" uri="http://sling.apache.org/taglibs/sitebuilder"%>
<%@ taglib prefix="sb2" uri="http://sling.apache.org/taglibs/sitebuilder2"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<sling:defineObjects />
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<title>Apache Sling Page Editor</title>

<link href="<%=request.getContextPath()%>/apps/sling/pageEditor/css/pageEditor.css" rel="stylesheet">
<link href="<%=request.getContextPath()%>/apps/sling/pageEditor/css/preview.css" rel="stylesheet">

<!-- Bootstrap -->
<link href="<%=request.getContextPath()%>/apps/sling/pageEditor/3rdparty/bootstrap.css" rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

<script>
	var contextPath = "${pageContext.request.contextPath}";
</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/apps/sling/pageEditor/3rdparty/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/apps/sling/pageEditor/js/behavior.js"></script>

</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div id="edit-col" class="col-xs-10">
				<div id="page-to-edit" class="">
					<%-- see: http://stackoverflow.com/questions/16731203/invalid-mouse-offset-for-cross-frame-draggable-sortable/31999152#31999152  --%>
					<iframe id="iframe_editor" src="${resource.path}.html" class="" allowtransparency="true" frameborder="0" height="100%"
						width="100%"></iframe>
				</div>
			</div>
			<div class="col-xs-2">
				<a href="${resource.path}.html" target="_blank"> <span id="open-new-window" class="glyphicon glyphicon-share-alt"
					aria-hidden="true"></span>
				</a>
				<c:set var="sitebuilder_apps_path" value="/apps/sling/sitebuilder" />
				<c:set var="components_path" value="${sitebuilder_apps_path}/components" />
				<c:set var="toolbar_query" value="/jcr:root${components_path}//element(*, sb:toolbar)" />
				<sling:findResources query="${toolbar_query}" language="xpath" var="toolbars" />
				<ul class="nav nav-pills pe-toolbar-menu" role="tablist">
					<c:forEach var="toolbar" items="${toolbars}" varStatus = "status">
						<c:if test="${not fn:startsWith(toolbar.name,'.')}">
							<sling:adaptTo adaptable="${toolbar}" adaptTo="org.apache.sling.api.resource.ValueMap" var="toolbarProps" />
							<c:set var="toolbar_id" value="${fn:substringAfter(toolbar.path, components_path)}" />
				  			<li role="presentation" class="${status.first ? 'active' : ''}"><a href="#${sling:encode(fn:replace(toolbar_id,'/','_'),'HTML_ATTR')}" data-toggle="pill">${toolbarProps.label}</a></li>
						</c:if>
					</c:forEach>
				</ul>
				<!-- Tab panes -->
				<div class="tab-content">
					<sling:findResources query="${toolbar_query}" language="xpath" var="toolbars" />
					<c:forEach var="toolbar" items="${toolbars}" varStatus="status">
						<c:set var="toolbar_id" value="${fn:substringAfter(toolbar.path, components_path)}" />
						<div role="tabpanel" class="tab-pane ${status.first ? 'active' : ''}" id="${sling:encode(fn:replace(toolbar_id,'/','_'),'HTML_ATTR')}">
							<c:forEach var="toolbar_item" items="${sling:listChildren(toolbar)}">
								<c:if test="${not fn:startsWith(toolbar_item.name,'.')}">
									<c:set var="component_type" value="${toolbar_id}/${toolbar_item.name}" />
									<c:set var="encoded_component_type" value="${sling:encode(fn:replace(component_type,'/','_'),'HTML_ATTR')}" />
									<li class="component-label" data-component-type="${component_type}"><a href="javascript:void(0)" class="drag small glyphicon glyphicon-move ui-draggable-handle"> ${toolbar_item.name}</a></li>
								</c:if>
							</c:forEach>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-2 sidebar-container">
				<h4>Web Component Palette</h4>

				<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-row-4-4-4">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true"
									aria-controls="collapse-bs-row-4-4-4"><span class="component-label" data-component-type="bs-row-4-4-4"> Row
										4-4-4 <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-row-4-4-4" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-row-4-4-4">
							<div class="panel-body">No 'Row 4-4-4' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-row-12">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="true"
									aria-controls="collapse-bs-row-12"><span class="component-label" data-component-type="bs-row-12"> Row 12 <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-row-12" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-row-12">
							<div class="panel-body">No 'Row 12' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-p">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="true"
									aria-controls="collapse-bs-p"><span class="component-label" data-component-type="bs-p"> Paragraph <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-p" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-p">
							<div class="panel-body">No 'Paragraph' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-h1">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="true"
									aria-controls="collapse-bs-h1"><span class="component-label" data-component-type="bs-h1"> H1 <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-h1" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-h1">
							<div class="panel-body">No 'H1' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-navbar">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSix" aria-expanded="true"
									aria-controls="collapse-bs-navbar"><span class="component-label" data-component-type="bs-navbar"> Navbar <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-navbar" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-navbar">
							<div class="panel-body">No 'Navbar' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-img">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="true"
									aria-controls="collapse-img"><span class="component-label" data-component-type="img"> Image <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-img" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-img">
							<div class="panel-body">No 'Image' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-sling-include">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#sling-include-entry" aria-expanded="true"
									aria-controls="sling-include-entry"> <span class="component-label" data-component-type="sling-include">
										Include Script <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="sling-include-entry" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-sling-include">
							<div class="panel-body">
								<form>
									<div class="checkbox">
										<label> <input name="copy" checked type="checkbox">Copy a prototype to that paths
										</label>
									</div>
									<div class="form-group">
										<label for="resource_path">Resource Path:</label> <input type="text" class="form-control" id="resource_path"
											name="resource_path" placeholder="Resource Path">
									</div>
									<div class="form-group">
										<label for="script_resource_path">Script Resource Path:</label> <input type="text" class="form-control"
											id="script_resource_path" name="script_resource_path" placeholder="Script Resource Path">
									</div>
									<div class="form-group">
										<label for="resource_type">Resource Type:</label> <input type="text" class="form-control" id="resource_type"
											name="resource_type" placeholder="Resource Type">
									</div>
								</form>
							</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-sling-call">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#sling-call-entry" aria-expanded="true"
									aria-controls="sling-call-entry"> <span class="component-label" data-component-type="sling-call"> Call Script
										<span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="sling-call-entry" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-sling-call">
							<div class="panel-body">
								<form>
									<div class="checkbox">
										<label> <input name="copy" checked type="checkbox">Copy a prototype to that path
										</label>
									</div>
									<div class="form-group">
										<label for="script_resource_path">Script Resource Path:</label> <input type="text" class="form-control"
											id="script_resource_path" name="script_resource_path" placeholder="Script Resource Path">
									</div>
								</form>
							</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-form">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#bs-form-entry" aria-expanded="true"
									aria-controls="bs-form-entry"> <span class="component-label" data-component-type="bs-form"> Form <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="bs-form-entry" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-form">
							<div class="panel-body">No form properties</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-text-field">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#bs-text-field-entry" aria-expanded="true"
									aria-controls="bs-text-field-entry"> <span class="component-label" data-component-type="bs-text-field"> Text
										Field <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="bs-text-field-entry" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-text-field">
							<div class="panel-body">No form properties</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-button-submit">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="true"
									aria-controls="collapse-bs-button-submit"><span class="component-label" data-component-type="bs-button-submit">
										Submit Button <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-button-submit" class="panel-collapse collapse" role="tabpanel"
							aria-labelledby="heading-bs-button-submit">
							<div class="panel-body">No 'Button' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-user-text-field">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="true"
									aria-controls="collapse-bs-user-text-field"><span class="component-label" data-component-type="bs-user-text-field">
										Text Field (value = username) <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-user-text-field" class="panel-collapse collapse" role="tabpanel"
							aria-labelledby="heading-bs-user-text-field">
							<div class="panel-body">No properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-hidden-field">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="true"
									aria-controls="collapse-bs-hidden-field"><span class="component-label" data-component-type="bs-hidden-field">
										Hidden Field <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-hidden-field" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-hidden-field">
							<div class="panel-body">No properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-text-link">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="true"
									aria-controls="collapse-bs-text-link"><span class="component-label" data-component-type="bs-text-link"> Text
										Link <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-text-link" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-text-link">
							<div class="panel-body">No properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="heading-bs-table">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="true"
									aria-controls="collapse-bs-table"><span class="component-label" data-component-type="bs-table"> Table <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapse-bs-table" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-bs-table">
							<div class="panel-body">No properties.</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="prototypes">

			<div class="component-wrapper">
				<div class="component-toolbar" data-toggle="tooltip" data-placement="bottom">
					<a href="javascript:void(0)" class="delete small glyphicon glyphicon-trash"></a>&nbsp;<a
						href="will be filled dynamically later" target="_blank" class="edit small glyphicon glyphicon-edit"></a>&nbsp;<a
						href="javascript:void(0)" class="drag small glyphicon glyphicon-move"></a>
				</div>
			</div>
<%-- 			<sling:include path="/sitebuilder/componentStock" /> --%>
			<div class="component-prototypes">
				<sling:findResources var="toolbars" query="${toolbar_query}" language="xpath" />
				<c:forEach var="toolbar" items="${toolbars}" varStatus="status">
					<c:set var="toolbar_id" value="${fn:substringAfter(toolbar.path, components_path)}" />
					<c:forEach var="toolbar_item" items="${sling:listChildren(toolbar)}">
						<c:if test="${not fn:startsWith(toolbar_item.name,'.')}">
							<c:set var="component_type" value="${toolbar_id}/${toolbar_item.name}" />
							<c:set var="component_content" value="${component_type}/${toolbar_item.name}-content" />
							<c:set var="component_path" value="${components_path}${toolbar_id}/${toolbar_item.name}" />
							<c:set var="component_content_path" value="${component_path}/${toolbar_item.name}-content" />
							<c:set var="encoded_component_type" value="${sling:encode(fn:replace(component_type,'/','_'),'HTML_ATTR')}" />
							<c:set var="theResourceType" value="${toolbar.path}/${toolbar_item.name}" />
							
							<div data-component-type="${component_type}" class="preview-container">
								<sb2:component resourceType="${theResourceType}" componentId="${component_content_path}" />
							</div>
							
						</c:if>
					</c:forEach>
				</c:forEach>
			</div>
		</div>
	</div>
	<%-- 	<script type="text/javascript" src="<%= request.getContextPath() %>/apps/sling/pageEditor/js/preview.js"></script> --%>
	<script type="text/javascript" src="<%=request.getContextPath()%>/apps/sling/pageEditor/3rdparty/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/apps/sling/pageEditor/3rdparty/bootstrap.min.js"></script>


	<!--         <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script> -->
	<!--         <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js"></script> -->

</body>
</html>