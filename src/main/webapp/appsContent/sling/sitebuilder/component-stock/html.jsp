<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="sb" uri="http://sling.apache.org/taglibs/sitebuilder"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<sling:defineObjects />

<div class="component-stock">
	<div id="bs-row-4-4-4" class="preview-container">
		<div>
			<div class="row component" data-component-type="bs-row-4-4-4" data-component-id="1">
				<div class="col-xs-4 component-container"></div>
				<div class="col-xs-4 component-container"></div>
				<div class="col-xs-4 component-container"></div>
			</div>
		</div>
		<div id="bs-row-12" class="preview-container">
			<div class="row component" data-component-type="bs-row-12" data-component-id="2">
				<div class="col-xs-12 component-container"></div>
			</div>
		</div>
		<div id="bs-h1" class="preview-container">
			<sb:component componentId="3">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<h1 class="component" data-component-type="bs-h1" ${componentIdAttribute}>
					<c:out value="${componentProps.value}" />
				</h1>
			</sb:component>
		</div>
		<div id="bs-p" class="preview-container">
			<sb:component componentId="4">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<p class="component" data-component-type="bs-p" ${componentIdAttribute}>
					<c:out value="${sling:getValue(componentProps,'value','Paragraph Content')}" />
				</p>
			</sb:component>
		</div>
		<div id="img" class="preview-container">
			<span class="component" data-component-type="img" data-component-id="5"> <img style="max-width: 100%; max-height: 100%;"
				src="" alt="Image"> <script type="text/javascript">
					// creating the namespace
					var org = org || {};
					org.carrental = org.carrental || {};
					//defining the module
					org.carrental.ImageLoader = org.carrental.ImageLoader || (function() {
						function ImageLoader() {
							var thatImageLoader = this;
							$('[data-component-type="img"]').each(function() {
								var thisComponent = this;
								org.sling.sitebuilder.client.getComponentJSON(this, function(data) {
									$(thisComponent).children("img:first").attr("src", data.src);
								});
							});
						}
						return ImageLoader;
					}());
					org.sling.sitebuilder.componentScripts["org.carrental.imageLoader"] = org.carrental.ImageLoader;
				</script>
			</span>
		</div>
		<div id="bs-navbar" class="preview-container">
			<sb:component componentId="6">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<nav class="component navbar navbar-default" data-component-type="bs-navbar" ${componentIdAttribute}>
					<div class="container-fluid">
						<div class="navbar-header">
							<a class="navbar-brand" href="/carRental/home.html"><c:out value="${componentProps.brand}" /></a>
						</div>
						<!-- Collect the nav links, forms, and other content for toggling -->
						<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
							<ul class="nav navbar-nav">
								<c:forEach var="child" items="${sling:listChildren(componentResource)}" varStatus="status">
									<sling:adaptTo adaptable="${child}" adaptTo="org.apache.sling.api.resource.ValueMap" var="childProps" />
									<li class="">
										<a href="${fn:escapeXml(childProps.href)}"><c:out value="${childProps.linkText}" /></a>
									</li>
								</c:forEach>
							</ul>
						</div>
						<!-- /.navbar-collapse -->
					</div>
					<!-- /.container-fluid -->
				</nav>
			</sb:component>
		</div>
		<div id="sling-include" class="preview-container">
			<div class="component script-container" data-component-type="sling-include" data-component-id="7">
				<sling:include path="/sitebuilder" resourceType="org/bootstrap" />
			</div>
		</div>
		<div id="sling-call" class="preview-container">
			<div class="component script-container" data-component-type="sling-call" data-component-id="8">
				<sling:call script="/apps/sling/pageEditor/prototype/call/call-prototype.jsp" />
			</div>
		</div>
		<div id="bs-form" class="preview-container">
			<sb:component componentId="9">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<div class="component" data-component-type="bs-form" ${componentIdAttribute}>
					<form class="component-container" action="${fn:escapeXml(componentProps.action)}" method="${componentProps.method}"></form>
				</div>
			</sb:component>
		</div>
		<div id="bs-text-field" class="preview-container">
			<sb:component componentId="10">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<div class="form-group component" data-component-type="bs-text-field" ${componentIdAttribute}>
					<label for="${componentProps.nameAndId}"><c:out value="${componentProps.label}" /></label> <input type="text"
						class="form-control" id="${componentProps.nameAndId}" name="${componentProps.nameAndId}" value="${componentProps.value}">
				</div>
			</sb:component>
		</div>
		<div id="bs-button-submit" class="preview-container">
			<sb:component componentId="11">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<button class="component btn btn-success" data-component-type="bs-button-submit" type="submit" class="btn btn-primary"
					${componentIdAttribute}>
					<c:out value="${componentProps.text}" />
				</button>
			</sb:component>
		</div>
		<div id="bs-user-text-field" class="preview-container">
			<sb:component componentId="12">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<input type="hidden" class="component" data-component-type="bs-user-text-field" ${componentIdAttribute} id="${componentProps.nameAndId}" name="${componentProps.nameAndId}"
					value="<%=request.getUserPrincipal().getName()%>">
			</sb:component>
		</div>
		<div id="bs-hidden-field" class="preview-container">
			<sb:component componentId="13">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<input type="hidden" class="component" data-component-type="bs-hidden-field" ${componentIdAttribute} id="${componentProps.nameAndId}" name="${componentProps.nameAndId}"
					value="${componentProps.value}">
			</sb:component>
		</div>
		<div id="bs-text-link" class="preview-container">
			<sb:component componentId="14">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<a href="${componentProps.href}"  class="component" data-component-type="bs-text-link" ${componentIdAttribute}><c:out value="${componentProps.linkText}" /></a>
			</sb:component>
		</div>
		<div id="bs-table" class="preview-container">
			<sb:component componentId="15">
				<sling:adaptTo adaptable="${componentResource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<c:set var="searchPath" value="${componentProps.searchPath}"></c:set>
				<sling:findResources query="/jcr:root${componentProps.searchPath}//element(*, nt:base)" language="xpath" var="bookings" />
				<table class="component table table-hover" data-component-type="bs-table" ${componentIdAttribute}>
					<tr>
						<c:forEach var="colDef" items="${sling:listChildren(componentResource)}">
							<sling:adaptTo adaptable="${colDef}" adaptTo="org.apache.sling.api.resource.ValueMap" var="colDefProps" />
				  			<th><c:out value='${colDefProps.colheader}'></c:out></th>
			  			</c:forEach>
					</tr>
					<c:forEach var="row" items="${bookings}" varStatus="status">
						<sling:adaptTo adaptable="${row}" adaptTo="org.apache.sling.api.resource.ValueMap" var="rowProps" />

						<tr>
							<c:forEach var="colDef" items="${sling:listChildren(componentResource)}" varStatus="status">
								<sling:adaptTo adaptable="${colDef}" adaptTo="org.apache.sling.api.resource.ValueMap" var="colDefProps" />
					  			<td><c:out value='${rowProps[colDefProps.colname]}'></c:out></td>
				  			</c:forEach>
						</tr>
					</c:forEach>
				</table>
			</sb:component>
		</div>
			
	</div>
</div>
