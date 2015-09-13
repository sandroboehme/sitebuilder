<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="sb" uri="http://sling.apache.org/taglibs/sitebuilder"%>

<sling:defineObjects />

			<div class="component-stock">
				<div id="bs-row-4-4-4" class="preview-container"><div>
					<div class="row component" data-component-type="bs-row-4-4-4">
						  <div class="col-xs-4 component-container"></div>
						  <div class="col-xs-4 component-container"></div>
						  <div class="col-xs-4 component-container"></div>
					</div>
				</div>
				<div id="bs-row-12" class="preview-container">
					<div class="row component" data-component-type="bs-row-12">
						  <div class="col-xs-12 component-container"></div>
					</div>
				</div>
				<div id="bs-h1" class="preview-container">
					<h1 class="component" data-component-type="bs-h1">h1. Bootstrap heading</h1>
				</div> 
				<div id="bs-p" class="preview-container">
					<sb:component componentId="4">
						<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
						<p class="component" data-component-type="bs-p" ${componentIdAttribute}>
							<c:out value="${sling:getValue(componentProps,'value','Paragraph Content')}" />
						</p>
					</sb:component>
				</div>
				<div id="bs-button" class="preview-container">
					<button class="component btn btn-success" data-component-type="bs-button" type="button" class="btn btn-success">Success</button>
				</div>
				<div id="img" class="preview-container">
					<img class="component" data-component-type="img" style="max-width: 100%; max-height: 100%;" src="http://images.freeimages.com/images/thumbs/23e/mercedes-1485180.jpg" alt="Mercedes">
				</div>
				<div id="bs-navbar" class="preview-container">
					<nav class="component navbar navbar-default" data-component-type="bs-navbar">
						<div class="container-fluid">
							<div class="navbar-header">
								<a class="navbar-brand" href="/carRental/home.html">Premium Car Rental</a>
							</div>
							<!-- Collect the nav links, forms, and other content for toggling -->
							<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
								<ul class="nav navbar-nav">
									<li class="active"><a href="/carRental/home.html">Home <span class="sr-only">(current)</span></a></li>
									<li><a href="/carRental/booking.html">Book</a></li>
								</ul>
							</div>
							<!-- /.navbar-collapse -->
						</div>
						<!-- /.container-fluid -->
					</nav>
				</div>
			</div>
		</div>
