<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling"%>
<%@ taglib prefix="sb" uri="http://sling.apache.org/taglibs/sitebuilder"%>
<%@ taglib prefix="sb2" uri="http://sling.apache.org/taglibs/sitebuilder2"%>
<sling:defineObjects />

				<sling:adaptTo adaptable="${resource}" adaptTo="org.apache.sling.api.resource.ValueMap" var="componentProps" />
				<button class="component btn btn-success" data-component-type="bs-button-submit" type="submit" class="btn btn-primary"
					${componentIdAttribute}>
					<c:out value="${componentProps.text}" />
				</button>
