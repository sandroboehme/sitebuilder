<!DOCTYPE html>

<%@ page session="false"%>
<%@ page isELIgnored="false"%>
<%@ page import="javax.jcr.*,org.apache.sling.api.resource.Resource"%>
<%@ page import="java.security.Principal"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="sb" uri="http://sling.apache.org/taglibs/sitebuilder"%>

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
			<a href="${resource.path}.html" target="_blank"> <span id="open-new-window" class="glyphicon glyphicon-share-alt"
				aria-hidden="true"></span>
			</a>
			<div class="col-xs-2 sidebar-container">
				<h4>Web Component Palette</h4>
				<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingOne">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true"
									aria-controls="collapseOne"><span class="component-label" data-component-type="bs-row-4-4-4"> Row 4-4-4 <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
							<div class="panel-body">No 'Row 4-4-4' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingTwo">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="true"
									aria-controls="collapseTwo"><span class="component-label" data-component-type="bs-row-12"> Row 12 <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
							<div class="panel-body">No 'Row 12' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingThree">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="true"
									aria-controls="collapseThree"><span class="component-label" data-component-type="bs-button"> Button <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
							<div class="panel-body">No 'Button' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingFour">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="true"
									aria-controls="collapseFour"><span class="component-label" data-component-type="bs-p"> Paragraph <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseFour" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFour">
							<div class="panel-body">No 'Paragraph' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingFive">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="true"
									aria-controls="collapseFive"><span class="component-label" data-component-type="bs-h1"> H1 <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseFive" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFive">
							<div class="panel-body">No 'H1' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingSix">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSix" aria-expanded="true"
									aria-controls="collapseSix"><span class="component-label" data-component-type="bs-navbar"> Navbar <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseSix" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingSix">
							<div class="panel-body">No 'Navbar' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingSeven">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="true"
									aria-controls="collapseSeven"><span class="component-label" data-component-type="img"> Image <span
										class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseSeven" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingSeven">
							<div class="panel-body">No 'Image' properties.</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="headingEight">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseEight" aria-expanded="true"
									aria-controls="collapseEight"> <span class="component-label" data-component-type="sling-include"> Include
										Script <span class="drag small glyphicon glyphicon-move"></span>
								</span></a>
							</h4>
						</div>
						<div id="collapseEight" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingEight">
							<div class="panel-body">
								<form>
									<div class="checkbox">
										<label> <input type="checkbox">Copy a prototype to that paths
										</label>
									</div>
									<div class="form-group">
										<label for="exampleInputEmail1">Resource Path:</label> <input type="email" class="form-control" id="exampleInputEmail1"
											placeholder="Email">
									</div>
									<div class="form-group">
										<label for="exampleInputPassword1">Script Resource Path:</label> <input type="password" class="form-control"
											id="exampleInputPassword1" placeholder="Password">
									</div>
									<button type="submit" class="btn btn-default">Submit</button>
								</form>
							</div>
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
			<sling:include path="/sitebuilder/componentStock" />
		</div>
	</div>
	<%-- 	<script type="text/javascript" src="<%= request.getContextPath() %>/apps/sling/pageEditor/js/preview.js"></script> --%>
	<script type="text/javascript" src="<%=request.getContextPath()%>/apps/sling/pageEditor/3rdparty/jquery-ui.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/apps/sling/pageEditor/3rdparty/bootstrap.min.js"></script>

</body>
</html>