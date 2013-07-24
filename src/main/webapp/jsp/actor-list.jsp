<%@ include file="/jsp/include.jsp"%>
<%@ include file="/jsp/head.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<title><c:out value="${model.movieTitle}" /> : IMDB powered by Neo4j</title>
</head>
<body>
<h1><c:out value="${model.movieTitle}" /> <c:if test="${model.movieRatings!=null && model.movieRatings!=\"\"}">(<c:out value="${model.movieRatings}" />/10)</c:if></h1>
<h3>Cast</h3>
${model.movieGenre}
<ul class="actors">
	<c:forEach items="${model.peopleInfo}" var="peopleInfo">
		<c:url value="actor.html" var="actorURL">
			<c:param name="name" value="${peopleInfo.name}" />
		</c:url>
		<li class="${fn:toLowerCase(peopleInfo.role)}"><a href='<c:out value="${actorURL}"/>'><c:out
			value="${peopleInfo.name}" /></a> 
                        <c:if test="${peopleInfo.character!=null}">
                            as <em><c:out
                            value="${peopleInfo.character}" /></em>
                        </c:if>
                        <c:if test="${peopleInfo.character==null}">
                            ${fn:toLowerCase(peopleInfo.role)}
                        </c:if>
                </li>
	</c:forEach>
</ul>

<%@ include file="/jsp/menu.jsp"%>
</body>
</html>
