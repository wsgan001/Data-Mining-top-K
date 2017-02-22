
/**
 * @author Z. Su
 * Implement Trie with lists.
 */


class TrieListNode {
    private TrieListNode[] children;
    public boolean isEnd;
    
    public TrieListNode() {
        children = new TrieListNode[10];
        isEnd = false;
    }
    
    public void insert(String word, int index) {
        if (index == word.length()) {
            this.isEnd = true;
            return;
        }
        
        int pos = word.charAt(index) - '0';
        if (children[pos] == null) {
            children[pos] = new TrieListNode();
        }
        children[pos].insert(word, index + 1);
    }
    
    public TrieListNode find(String word, int index) {
        if (index == word.length()) {
            return this;
        }
        
        int pos = word.charAt(index) - '0';
        if (children[pos] == null) {
            return null;
        }
        return children[pos].find(word, index + 1);
    }
}

public class TrieList {
    private TrieListNode root;

    public TrieList() {
        root = new TrieListNode();
    }

    // Inserts a word into the trie.
    public void insert(String word) {
        root.insert(word, 0);
    }

    // Returns if the word is in the trie.
    public boolean search(String word) {
        TrieListNode node = root.find(word, 0);
        return (node != null && node.isEnd);
    }
}