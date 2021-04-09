package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点(不存放任何字符)
    private TrieNode rootNode = new TrieNode();


    @PostConstruct //
    public void init(){
        // 写在try里自动关掉
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); // 把字节流转换成缓冲流(读写效率高)
        ){
               String keyword;
               while((keyword = reader.readLine())!=null){
                   // 添加到前缀树
                   this.addKeyWord(keyword);
               }
        }catch(IOException e){
            logger.error("加载敏感词文件失败:"+e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中
    public void addKeyWord(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c); // 找一下之前是否有挂过该字符
            if(subNode==null){
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            // 若改字符节点已存在,直接跳过
            // 指向子节点,进入下一轮循环
            tempNode = subNode ;
            // 设置敏感词结束标识
            if(i==keyword.length()-1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /***
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = rootNode ;
        // 指针2
        int begin = 0 ;
        // 指针3
        int position = 0 ;
        // 结果
        StringBuilder sb = new StringBuilder();
        while(begin<text.length()) {
            if(position < text.length()) {
                Character c = text.charAt(position);

                if (isSymbol(c)) {
                    // 跳过符号
                    if(tempNode==rootNode){ // 是符号且树指针位于根节点,当前检查还没有开始
                        sb.append(c);
                        begin++;
                    }
                    position++;
                    continue;
                }

                // c不是符号
                // 检查下级结点
                tempNode = tempNode.getSubNode(c); // 检查是否存在该字符,若存在tempNode指向该结点
                if(tempNode==null){
                    // 以begin为开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    position = ++begin ; // position回到原位置
                    tempNode = rootNode; // 树指针回到原位置
                }
                else if(tempNode.isKeyWordEnd){  // 是敏感词的结尾
                    // 发现敏感词,将begin~position替换掉
                    sb.append(REPLACEMENT);
                    begin = ++position;
                    tempNode = rootNode ;
                }else{
                    // 是树的路径上的一部分,检查下一个字符
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }
        }
        return sb.toString();
    }

    // 判断是否为特殊符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF 东亚文字范围
        // 不是正常文字且不属于东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }



    // 前缀树结点结构
    private class TrieNode{
        // (敏感)关键词结束的标识
        private boolean isKeyWordEnd = false ;
        // 当前结点的子节点(key下级字符,value是指向下级结点的指针)
        private Map<Character,TrieNode> subNodes = new HashMap<>();
        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }
        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
        // 添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }



}
