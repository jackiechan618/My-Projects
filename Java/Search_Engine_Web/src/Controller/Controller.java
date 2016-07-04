package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String keywords;
	String[] keywordArray;
	String[] urlArray;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		Service(action, request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		Service(action, request, response);
	}

	public void Service(String action, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		if(action == null){
			request.getRequestDispatcher("/HomePage.jsp").forward(request, response);
		}
		else if(action.equals("searching")){
			keywords = request.getParameter("keywords");
			if(!keywords.equals("")){
				keywordArray = keywords.split("\\s{1,}");
				calculateScore myCalculateScore = new calculateScore(52140, keywordArray);
				urlArray = myCalculateScore.getDocScoreFunction();
				request.setAttribute("urlArray", urlArray);
				request.getServletContext().getRequestDispatcher("/searchPage.jsp").forward(request, response);	
			}
			else{
				request.getServletContext().getRequestDispatcher("/HomePage.jsp").forward(request, response);	
			}
		}
		else ;
	}
}
