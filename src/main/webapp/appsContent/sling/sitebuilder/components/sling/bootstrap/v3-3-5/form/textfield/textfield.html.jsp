<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="sb" uri="http://sling.apache.org/taglibs/sitebuilder"%>
<%@ taglib prefix="sb2" uri="http://sling.apache.org/taglibs/sitebuilder2"%>
<sling:defineObjects />

				<sling:adaptTo adaptable="${resource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<div class="form-group component" data-component-type="bs-text-field" ${componentIdAttribute}>
					<label for="${componentProps.nameAndId}"><c:out value="${componentProps.label}" /></label> 
					<input type="text" class="form-control" id="${componentProps.nameAndId}" name="${componentProps.nameAndId}" value="${componentProps.value}">
				</div>
