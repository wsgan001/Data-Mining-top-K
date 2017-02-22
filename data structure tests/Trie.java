/*
 * @author: Z. Su
 * Implement Trie with hashtables.
 */

import java.util.HashMap;




class TrieNode {
    char c;
    HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    boolean isEnd;
    
    public TrieNode(){       
    }
    
    public TrieNode(char c){
        this.c = c;
    }
}

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a string into the trie.
    public void insert(String word) {
        TrieNode curr = root;
        HashMap<Character, TrieNode> currChildren = root.children;
        char[] wordArray = word.toCharArray();
        for(int i = 0; i < wordArray.length; i++){
            char currChar = wordArray[i];
            if(currChildren.containsKey(currChar)){
                curr = currChildren.get(currChar);
            } else {
                TrieNode newNode = new TrieNode(currChar);
                currChildren.put(currChar, newNode);
                curr = newNode;
            }
            currChildren = curr.children;
            if(i == wordArray.length - 1){
                curr.isEnd= true;
            }
        }
    }

    // Returns if the word is in the trie.
    public boolean search(String word) {
        if(searchWordNodePos(word) == null){
            return false;
        } else if(searchWordNodePos(word).isEnd) 
          return true;
          else return false;
    }
    
    public TrieNode searchWordNodePos(String word){
        HashMap<Character, TrieNode> children = root.children;
        TrieNode curr = null;
        char[] wordArray = word.toCharArray();
        for(int i = 0; i < wordArray.length; i++){
            char c = wordArray[i];
            if(children.containsKey(c)){
                curr = children.get(c);
                children = curr.children;
            } else{
                return null;
            }
        }
        return curr;
    }
}