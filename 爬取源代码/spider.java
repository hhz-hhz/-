package software_test;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class spider {
    
	public boolean IsNumber(char x) {
		switch (x) {
		case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':
			return true;
		default:
			return false;
		}
	}
	
	public int string_to_int(String src) {
		int ans=0,len,r=1;
		len = src.length();
		char[] num = src.toCharArray();
		for(;len>0;) {
			ans+=(num[--len]-'0')*r;
			r*=10;
		}
		return ans;
	}
	
    public void getSubmission(String problemNum, String problemChar, String submissionId) throws IOException {
    	String urlString = "http://codeforces.com/problemset/submission/" + problemNum + "/" + submissionId;
        Document doc = Jsoup.connect(urlString).get();
        System.out.println("subs-title = " + doc.title());
        
        String dirString = "D:/codeforces/" + problemNum + problemChar;
        File dir = new File(dirString);
        if (dir.exists() == false) dir.mkdirs();
        
        // 获取提交的代码
        String codeString = doc.getElementById("program-source-text").text();
        File codeFile = new File(dirString + File.separator + submissionId + ".java");
        if(codeFile.exists() == true) return;
        FileHelper.writeFile(codeFile, codeString);
    }
	
	public void getTestcase(String problemNum, String problemChar) throws IOException{
		String urlString = "http://codeforces.com/problemset/problem/" + problemNum + "/" + problemChar;
		Document doc = Jsoup.connect(urlString).get();
		String dirString = "D:/codeforces/" + problemNum + problemChar + "/UsingCase";
		File dir = new File(dirString);
        if (dir.exists() == false) dir.mkdirs();
        else return;//判断之前抓取过该提交代码，终止爬虫
        
        Elements code= doc.getElementsByClass("input");
        Elements pElements = code.select("pre");
        File codeFile = new File(dirString + File.separator + "input.txt");
        String codeString = null;
        int count = 0;
        for (Element pElement  : pElements) {
        	String pElementString=pElement.text();
        	pElementString=pElementString+"\r\n"+"\r\n";
        	if(count==0)codeString=pElementString;
        	else codeString=codeString+pElementString;
        	count=1;
        }
        FileHelper.writeFile(codeFile, codeString);
        
        code= doc.getElementsByClass("output");
        pElements = code.select("pre");
        codeFile = new File(dirString + File.separator + "output.txt");
        codeString = null;
        count = 0;
        for (Element pElement  : pElements) {
        	String pElementString=pElement.text();
        	pElementString=pElementString+"\r\n"+"\r\n";
        	if(count==0)codeString=pElementString;
        	else codeString=codeString+pElementString;
        	count=1;
        }
        FileHelper.writeFile(codeFile, codeString);
	}

	public void getJavainPage(int page,String problemNum,String problemChar) throws IOException{
		String urlString = "https://codeforces.com/problemset/status/" + problemNum +"/problem/" + problemChar + "/page/" +page+ "?order=BY_PROGRAM_LENGTH_ASC";
    	System.out.println("find java from: " + urlString);
    	Document doc = Jsoup.connect(urlString).get();
    	System.out.println("javas-title = " + doc.title());
    	
    	Elements trList = doc.select("tbody>tr");
    	Elements javaList = trList.select("tr:contains(Java 8)");
    	int sz = javaList.size();
    	
    	//checkpoint
    	System.out.println("size = "+sz);
    	
    	for(int i=0;i<sz;i++) {
    		String submitId = javaList.get(i).attr("data-submission-id");
    		
        	//checkpoint
        	System.out.println("submitId = "+ submitId);
            
            getSubmission( problemNum,  problemChar,  submitId);        	
    	}
	}
    
    public void getJavaSubmissionId(String problemNum,String problemChar) throws IOException{
    	String urlString = "https://codeforces.com/problemset/status/" + problemNum +"/problem/" + problemChar;
    	System.out.println("find java from: " + urlString);
    	Document doc = Jsoup.connect(urlString).get();
    	System.out.println("javas-title = " + doc.title());
    	
    	Elements pagination = doc.select("span.page-index");
    	int last = pagination.size();
    	
    	//checkpoint
    	System.out.println("last = " + last);

    	Elements trList = doc.select("tbody>tr");
    	Elements javaList = trList.select("tr:contains(Java 8)");
    	int sz = javaList.size();
    	
    	//checkpoint
    	System.out.println("size = "+sz);
    	
    	for(int i=0;i<sz;i++) {
    		String submitId = javaList.get(i).attr("data-submission-id");
    		
        	//checkpoint
        	System.out.println("submitId = "+ submitId);
            
            getSubmission( problemNum,  problemChar,  submitId);        	
    	}
    	
    	if(last!=0) {
    		String pagenum = pagination.get(last-1).text();
    		int pagemax = string_to_int(pagenum);
        	
        	//checkpoint
        	System.out.println("pagenum = " + pagenum);
        	System.out.println("pagemax = " + pagemax);
        	
        	//方便插入断点重入
        	int i=2;
        	for(;i <= pagemax;i++) {
        		getJavainPage(i,problemNum,problemChar);
    	}
    	}
    }
    
    
    public void getProblemId() throws IOException {
    	String urlString = "https://codeforces.com/problemset";
    	Document doc = Jsoup.connect(urlString).get();
        
        //将获取的问题编号输出到文件中查看
        String dirString = "D:/codeforces/";
        File dir = new File(dirString);
        if (dir.exists() == false) dir.mkdirs();
        File problemList = new File(dirString + File.separator + "problemId.txt");
        
    	Elements problemLink = doc.select("td.id > a");//获取所有的题目
    	int sz = problemLink.size();
    	
    	//checkpoint
    	System.out.println("size = "+sz);
    	//方便插入断点重入
    	int i=21;
    	for(;i<sz;i++) {
    		String problemId = problemLink.get(i).text();
    		int len = problemId.length();
    		
        	//checkpoint
        	System.out.println("problemId = "+problemId);
    		//checkpoint
            FileHelper.appendFile(problemList, problemId+'\n');
    		
    		String problemNum = new String();
    		String problemChar = new String();
    		if(IsNumber(problemId.charAt(len-1))) {//如果问题ID的最后一位是数字，说明形如“1288C3”
    			problemNum = problemId.substring(0,len-2);
    			problemChar = problemId.substring(len-2);
    			}
    		else {
    			problemNum = problemId.substring(0,len-1);
    			problemChar = problemId.substring(len-1);
    		}
    		
    		getTestcase(problemNum,problemChar);
    		getJavaSubmissionId(problemNum,problemChar);

    	}
    				

    }
    
    
    // main for test
    public static void main(String[] args) throws IOException {
    	new spider().getProblemId();
    }
}
