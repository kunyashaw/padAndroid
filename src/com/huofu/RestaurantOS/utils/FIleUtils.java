package com.huofu.RestaurantOS.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;

public class FIleUtils {
	private Context context; 
	//** SD卡是否存在** 
	private boolean hasSD = false; 
	
	//** SD卡的路径**
	private String SDPATH; 

	//** 当前程序包的路径** 
	private String FILESPATH; 
	
	public FIleUtils(Context context) { 
		this.context = context; 
		hasSD = Environment.getExternalStorageState().equals( 
		android.os.Environment.MEDIA_MOUNTED); 
		SDPATH = Environment.getExternalStorageDirectory().getPath(); 
		FILESPATH = this.context.getFilesDir().getPath(); 
	} 
	
	
	/** 
	* 在SD卡上创建文件 
	* 
	* @throws IOException 
	*/ 
	public File createSDFile(String fileName) throws IOException { 
		File file = new File(SDPATH + "//" + fileName); 
		if (!file.exists()) 
		{ 
			file.createNewFile(); 
		} 
		return file; 
	} 
	
	
	
	public File createFile(String fileName) throws IOException { 
		File file = new File(fileName); 
		if (!file.exists()) 
		{ 
			file.createNewFile(); 
		} 
		return file; 
	} 
	
	
	/** 
	* 删除SD卡上的文件 
	* 
	* @param fileName 
	*/ 
	public boolean deleteSDFile(String fileName) { 
		File file = new File(SDPATH + "//" + fileName); 
		if (file == null || !file.exists() || file.isDirectory()) 
		return false; 
		return file.delete(); 
	} 
	
	
	public static boolean deleteFile(String fileName)
	{
		File file = new File(fileName); 
		if (file == null || !file.exists() || file.isDirectory()) 
		return false; 
		return file.delete();
	}
	
	
	/** 
	* 写入内容到SD卡中的txt文本中 
	* str为内容 
	*/ 
	public void writeSDFile(String str,String fileName) 
	{ 
		try { 
			FileWriter fw = new FileWriter(SDPATH + "//" + fileName); 
			File f = new File(SDPATH + "//" + fileName); 
			fw.write(str); 
			FileOutputStream os = new FileOutputStream(f); 
			DataOutputStream out = new DataOutputStream(os); 
			out.writeShort(2); 
			out.writeUTF(""); 
			System.out.println(out); 
			fw.flush(); 
			fw.close(); 
			System.out.println(fw); 
		} catch (Exception e) { 
		} 
	} 
	
	
	  /**
     * write file
     * 
     * @param filePath
     * @param content
     * @param append is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file
     * 
     * @param filePath
     * @param contentList
     * @param append is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, List<String> contentList, boolean append) {
        if (ListUtils.isEmpty(contentList)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            int i = 0;
            for (String line : contentList) {
                if (i++ > 0) {
                    fileWriter.write("\r\n");
                }
                fileWriter.write(line);
            }
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file, the string will be written to the begin of the file
     * 
     * @param filePath
     * @param content
     * @return
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * write file, the string list will be written to the begin of the file
     * 
     * @param filePath
     * @param contentList
     * @return
     */
    public static boolean writeFile(String filePath, List<String> contentList) {
        return writeFile(filePath, contentList, false);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     * 
     * @param filePath
     * @param stream
     * @return
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     * 
     * @param file the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     * 
     * @param file
     * @param stream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     * 
     * @param file the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }
    
    /***
    * @param filePath
    * @return true if the necessary directories have been created or the target directory already exists, false one of
    *         the directories can not be created.
    *         <ul>
    *         <li>if {@link FileUtils#getFolderName(String)} return null, return false</li>
    *         <li>if target directory already exists, return true</li>
    *         <li>return {@link java.io.File#makeFolder}</li>
    *         </ul>
    */
   public static boolean makeDirs(String filePath) {
       String folderName = getFolderName(filePath);
       if (StringUtils.isEmpty(folderName)) {
           return false;
       }

       File folder = new File(folderName);
       return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
   }

   /**
    * @param filePath
    * @return
    * @see #makeDirs(String)
    */
   public static boolean makeFolders(String filePath) {
       return makeDirs(filePath);
   }
   
   
   public static String getFolderName(String filePath) {

       if (StringUtils.isEmpty(filePath)) {
           return filePath;
       }

       int filePosi = filePath.lastIndexOf(File.separator);
       return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
   }
	
	
	
	@Deprecated
	/** 
	* 读取SD卡中文本文件 
	* 
	* @param fileName 
	* @return 
	*/ 
	public String readSDFile(String fileName) { 
		
		StringBuffer sb = new StringBuffer(); 
		try {
			FileInputStream is;
			is = new FileInputStream(fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String line=null;  
			while((line=in.readLine())!=null)  
			{  
				
				line = line.replace("DEBUG", "\n\n");
				Pattern pattern = Pattern.compile("[0-9]{4}[-|\\-|/][0-9]{1,2}[-|\\-|/][0-9]{1,2} [0-9]{2}[:|\\-|/][0-9]{1,2}[:|\\-|/][0-9]{1,2},[0-9]{1,3}");
				Matcher matcher = pattern.matcher(line);
				matcher.find();
				
				//line = line.replace("] -", "]<br>");
				//line = line.replace("DEBUG", "\n");
				/*
		        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		        Matcher matcher = pattern.matcher(line);
		        
		        List<String> result=new ArrayList<String>();
				while(matcher.find()){
					result.add(matcher.group());
				}
				for(String s1:result){
					System.out.println(s1);
					CommonUtils.LogWuwei("regularExperison", s1);
					line = line.replace(s1, "");
				}
				
/*			    
		        pattern = Pattern.compile("[0-9]{4}[-|\\-|/][0-9]{1,2}[-|\\-|/][0-9]{1,2} [0-9]{2}[:|\\-|/][0-9]{1,2}[:|\\-|/][0-9]{1,2},[0-9]{1,3}");
		        matcher = pattern.matcher(line);
		        
		        result=new ArrayList<String>();
		        
				while(matcher.find()){
					result.add(matcher.group());
				}
				for(String s1:result){
					System.out.println(s1);
					line = line.replace(s1, "\n");
				}*/
				sb.append(line);
			}
			in.close();  
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			}  
		
		return sb.toString(); 
		
		/*StringBuffer sb = new StringBuffer(); 
		File file = new File(SDPATH + "//" + fileName); 
		try { 
		FileInputStream fis = new FileInputStream(file); 
		int c; 
		boolean flag = true;
		while ((c = fis.read()) != -1) { 
			if(c == '[')
			{
				flag = false;
				sb.append("\n\n\n");
			}
			else if(c == ']')
			{
				flag = true;
				sb.append("\n\n\n");
			}
			
			if(c == '-')
			{
				sb.append(10);
			}
			if(flag)
			{
				sb.append((char) c);
				
			}
					
			 
			
			
		}
		
		fis.close(); 
		} catch (FileNotFoundException e) { 
		e.printStackTrace(); 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} */
//		return sb.toString(); 
	} 
	
	
	public static StringBuilder readFile(String filePath, String charsetName) 
	{
	    
	        File file = new File(filePath);
	        StringBuilder fileContent = new StringBuilder("");
	        if (file == null || !file.isFile()) {
	            return null;
	        }

	        BufferedReader reader = null;
	        try {
	            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
	            reader = new BufferedReader(is);
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                if (!fileContent.toString().equals("")) {
	                    fileContent.append("\r\n");
	                }
	                fileContent.append(line);
	            }
	            reader.close();
	            return fileContent;
	        } catch (IOException e) {
	            throw new RuntimeException("IOException occurred. ", e);
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e) {
	                    throw new RuntimeException("IOException occurred. ", e);
	                }
	            }
	        }
	    }

	
	 public  String codeString(String fileName) throws Exception{ 
		 BufferedInputStream bin = new BufferedInputStream( new FileInputStream(SDPATH + "//" +fileName));   
		 int p = (bin.read() << 8) + bin.read();        
		 String code = null;                
		 switch (p) {            
			 case 0xefbb:          
				 code = "UTF-8";                
				 break;            
			 case 0xfffe:                
				 code = "Unicode";                
				 break;            
			 case 0xfeff:                
				 code = "UTF-16BE";                
				 break;            
			 default:                
				 code = "GBK";        
						 }                
		 return code;    
		 }
	
	public String getFILESPATH() { 
		return FILESPATH; 
	}
	
	
	public String getSDPATH() { 
		return SDPATH; 
	}
	
	
	public boolean hasSD() { 
		return hasSD; 
	} 
	
	public boolean isExist(String path)
	{
		File f=new File(path);
        return f.exists();
	}
}
