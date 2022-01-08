package com.aem.community.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.LoginException;
import javax.jcr.query.Query;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;

@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "= Find component Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.extensions=" + ".json",
		"sling.servlet.paths=" + "/bin/findcomponents", })
public class FindComponentsServlet extends SlingAllMethodsServlet {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private SlingRepository repository;

	private static final long serialVersionUID = 1L;
	public Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private QueryBuilder queryBuilder;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		Set<String> set = new HashSet<String>();
		String queryString = req.getParameter("res");
		try (ResourceResolver resourceResolver = getResourceResolverForPage("datawrite")) {
			Iterator<Resource> result = resourceResolver
					.findResources("SELECT [sling:resourceType] FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["
							+ queryString + "]) and [sling:resourceType] like '%%'", Query.JCR_SQL2);
			while (result.hasNext()) {
				Resource componentResource = result.next();
				set.add(componentResource.getResourceType());
			}

		} finally {
			System.out.println("I am cool");
		}

		resp.setContentType("text/html");
		resp.getWriter().write("<b>Below components are used = </b><br /><br />");
		for (String s : set) {
			resp.getWriter().write(s + "<br />");
		}
	}

	public ResourceResolver getResourceResolverForPage(String name) {
		ResourceResolver resourceResolver = null;
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(ResourceResolverFactory.SUBSERVICE, name);
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
		} catch (Exception e) {
		}
		return resourceResolver;
	}

}
