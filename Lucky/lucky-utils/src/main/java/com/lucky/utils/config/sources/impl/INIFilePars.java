package com.lucky.utils.config.sources.impl;


import com.lucky.utils.file.Resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class INIFilePars {


	private Map<String,Map<String,String>> iniMap;
	
	//当前节
	private String currSection;

	
	//是否存在换行符"\"
	private boolean isNewline=false;
	
	//多行配置中的key
	private String newLineKey;
	
	//多行配置中的value
	private StringBuilder newLineValue;
	
	//文件输入流
	private final InputStream iniInputStream;
	


	public INIFilePars(InputStream iniInputStream){
		this.iniInputStream = iniInputStream;
	}
	
	public INIFilePars(String iniFilePath) {
		this(Resources.getInputStream(iniFilePath));
	}

	public Map<String, Map<String, String>> getIniMap() {
		if(iniMap == null){
			iniMap = new HashMap<>();
			pars();
		}
		return iniMap;
	}
	
	public boolean iniExist() {
		return iniInputStream!=null;
	}
	
	private void pars() {
		if(iniInputStream!=null) {
			Map<String,String> kvMap=new HashMap<>();
			try(InputStreamReader isr = new InputStreamReader(iniInputStream, StandardCharsets.UTF_8);
				BufferedReader read = new BufferedReader(isr)) {
				//当前行
				String currLine;
				while((currLine = read.readLine()) != null) {
					if(currLine.contains(";")) {
						currLine = currLine.substring(0, currLine.indexOf(";"));
					}
					if(currLine.contains("#")) {
						currLine = currLine.substring(0, currLine.indexOf("#"));
					}
					if(currLine.startsWith(";")|| currLine.startsWith("#")) {
						continue;
					}else if(currLine.startsWith("[")&& currLine.endsWith("]")) {
						currSection= currLine.substring(1, currLine.length()-1);
						if(iniMap.containsKey(currSection)) {
							throw new RuntimeException(".ini配置文件内容格式不正确,存在两个相同的Section:["+currSection+"]");
						}
						iniMap.put(currSection,new HashMap<>());
						continue;
					}else if(!currLine.endsWith("\\")&&!isNewline) {//不是以"\"结尾，而且之前也不存在以"\"结尾的行
						if(currLine.contains("=")) {
							currLine = currLine.replaceFirst("=", "%Lucky%FK@7075&XFL");
							String[] KV = currLine.split("%Lucky%FK@7075&XFL");
							if(iniMap.containsKey(currSection)) {
								kvMap=iniMap.get(currSection);
								kvMap.put(KV[0], KV[1]);
							}else {
								kvMap.put(KV[0], KV[1]);
								iniMap.put(currSection, kvMap);
							}
						}
					}else if(currLine.endsWith("\\")&&!isNewline&& currLine.contains("=")) {//是以"\"结尾，而且之前不存在以"\"结尾的行
						currLine = currLine.replaceFirst("=", "%Lucky%FK@7075&XFL");
						String[] KV = currLine.split("%Lucky%FK@7075&XFL");
						isNewline=true;
						newLineKey=KV[0];
						newLineValue=new StringBuilder(KV[1].subSequence(0, KV[1].length()-1));
					}else if(currLine.endsWith("\\")&&isNewline) {//是以"\"结尾，而且存在以"\"结尾的行
						currLine = currLine.replaceAll("\\t", " ");
						int index=firstNoSpaceIndex(currLine);
						if(index==0||index==1) {
							newLineValue.append(currLine.substring(0, currLine.length()-1));
						} else {
							newLineValue.append(currLine.substring(index-1, currLine.length()-1));
						}
					}else if(!currLine.endsWith("\\")&&isNewline) {//不是以"\"结尾，而且存在以"\"结尾的行
						kvMap=iniMap.get(currSection);
						currLine = currLine.replaceAll("\\t", " ");
						int index=firstNoSpaceIndex(currLine);
						if(index==0||index==1) {
							newLineValue.append(currLine);
						} else {
							newLineValue.append(currLine.substring(index-1, currLine.length()));
						}
						kvMap.put(newLineKey, newLineValue.toString());
						iniMap.put(currSection, kvMap);
						isNewline=false;
					}
					else {
						continue;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isHasSection(String section) {
		return iniMap.containsKey(section);
	}
	
	public boolean isHasKey(String section,String key) {
		if(isHasSection(section)) {
			return iniMap.get(section).containsKey(key);
		}else {
			return false;
		}
	}
	
	public Map<String,String> getSectionMap(String section){
		if(iniMap.containsKey(section)) {
			return iniMap.get(section);
		}
		return null;
	}
	
	public String getValue(String section,String key) {
		Map<String, String> sectionMap = getSectionMap(section);
		if(sectionMap!=null) {
			if(sectionMap.containsKey(key)) {
				return sectionMap.get(key);
			}
			return null;
		}
		return null;
	}
	
	private static int firstNoSpaceIndex(String str) {
		char[] charArr = str.toCharArray();
		int i=0;
		boolean isHave=false;
		for(char ch:charArr) {
			if(ch!=' ') {
				isHave=true;
				break;
			}
			i++;
		}
		return isHave?i:-1;
	}
}

