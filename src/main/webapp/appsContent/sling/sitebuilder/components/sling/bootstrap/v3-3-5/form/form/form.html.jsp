<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<sling:defineObjects />

				<sling:adaptTo adaptable="${resource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<div class="component" data-component-type="bs-form" ${componentIdAttribute}>
					<form class="component-container" action="${fn:escapeXml(componentProps.action)}" method="${componentProps.method}"></form>
				</div>
