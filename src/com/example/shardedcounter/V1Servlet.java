package com.example.shardedcounter;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class V1Servlet extends HttpServlet {
	public final void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		String action = req.getParameter("action");
		ShardedCounter counter = new ShardedCounter();
		if ("increment".equals(action)) {
			counter.increment();
			resp.getWriter().println("Counter incremented");
		} else {
			resp.getWriter().print("getCount()->" + counter.getCount());
		}
	}
}
