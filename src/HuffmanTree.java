import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class HuffmanTree implements HuffmanInterface {

    // Completed huffman tree.
    private BinaryTree<HuffData> huffTree;

    private ArrayList<FrequencyTable> dataTable;

    // Array of accepted characters from message.
    private Character[] validCharacters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                                           't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                                           'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
                                           '5', '6', '7', '8', '9', ' ', '\t', '\n', '!', '.', '?'};


    /**
     * String constructor creates a custom huffman tree for the String passed
     * as a parameter. Also creates a dataTable that consists of the symbols
     * and their corresponding huffman code for quick and easy decoding.
     *
     * @param message String from which the custom huffman tree is built.
     */
    public HuffmanTree(String message) {
        HuffData[] symbols = createSymbols(message);
        buildTree(symbols);
        this.dataTable = new ArrayList<FrequencyTable>();
        buildFrequencyTable(huffTree.root);
    }




    /**
     * Decodes a message using the generated Huffman tree, where each character in the given message ('1's and '0's)
     * corresponds to traversals through said tree.
     *
     * @param codedMessage The compressed message based on this Huffman tree's encoding
     * @return The given message in its decoded form
     */
    public String decode(String codedMessage) {
        if(codedMessage == null)
            return null;

        StringBuilder sb = new StringBuilder();
        BinaryTree<HuffData> currentTree = huffTree;

        for(int i = 0; i < codedMessage.length(); i++) {
            if(codedMessage.charAt(i) == '1') {
                currentTree = currentTree.getRightSubtree();
            }
            else {
                currentTree = currentTree.getLeftSubtree();
            }

            if(currentTree.isLeaf()) {
                HuffData theData = currentTree.getData();
                sb.append(theData.symbol);
                currentTree = huffTree;
            }
        }
        return sb.toString();
    }



    /**
     * Outputs the message encoded from the generated Huffman tree.
     * pre: the Huffman tree has been built using characters by which the message is only comprised.
     *
     * @param message The message to be decoded
     * @return The given message in its specific Huffman encoding of '1's and '0's
     */
    public String encode(String message) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < message.length(); i++) {
            char currentCharacter = message.charAt(i);
            for(int j = 0; j < dataTable.size(); j++) {
                char currentSymbol = dataTable.get(j).symbol;
                if(currentCharacter == currentSymbol) {
                    String temp = dataTable.get(j).frequency;
                    sb.append(temp);
                }
            }
        }
        return sb.toString();
    }




    /**
     * A helper method that build the custom huffman tree.
     *
     * @param symbols array of Huffman objects
     */
    private void buildTree(HuffData[] symbols) {
        //Priority queue that takes initial capacity and how you will compare the values
        Queue<BinaryTree<HuffData>> theQueue = new PriorityQueue<BinaryTree<HuffData>>(symbols.length, new CompareHuffmanTrees());

        for(HuffData nextSymbol : symbols) {
            theQueue.add(new BinaryTree<HuffData>(nextSymbol, null, null)); //nulls are left and right
        }

        while(theQueue.size() > 1) {
            BinaryTree<HuffData> left = theQueue.poll();
            BinaryTree<HuffData> right = theQueue.poll();

            double wL = left.getData().weight;
            double wR = right.getData().weight;

            HuffData sum = new HuffData(wL + wR, null);

            BinaryTree<HuffData> newTree = new BinaryTree<HuffData>(sum, left, right);

            theQueue.offer(newTree);
        }

        huffTree = theQueue.poll();
    }




    /**
     * Helper method that counts the frequency of the accepted characters in a string and returns
     * an array of nodes containing the symbol and weight.
     *
     * @param message sting being analyzed.
     * @return HuffmanData array of nodes with their symbols and frequencies.
     */
    private HuffData[] createSymbols(String message) {
        ArrayList<HuffData> symbols = new ArrayList<>();
        for(int i = 0; i < validCharacters.length; i++) {
            char validTemp = validCharacters[i];
            int count = 0;
            for(int j = 0; j < message.length(); j++) {
                char chTemp = message.charAt(j);
                if(chTemp == validTemp)
                    count++;
            }
            if(count > 0)
                symbols.add(new HuffData(count, validTemp));
        }
        HuffData[] data = new HuffData[symbols.size()];
        for(int i = 0; i < data.length; i++) {
            char chTemp = symbols.get(i).symbol;
            double freqTemp = symbols.get(i).weight;
            data[i] = new HuffData(freqTemp, chTemp);
        }
        return data;
    }




    /**
     * Wrapper method for the recursive buildFrequencyTable method.
     *
     * pre - HuffmanTree is already built.
     * @param root root node of the huffman tree.
     */
    private void buildFrequencyTable(BinaryTree.Node<HuffData> root) {
        String s = "";
        buildFrequencyTable(root, s);
    }



    /**
     * Traverses through the huffman tree and obtains the huffman codes for each symbol
     * and stores the values in a table.
     * @param node
     * @param s
     */
    private void buildFrequencyTable(BinaryTree.Node<HuffData> node, String s) {
        if(node.left != null && node.right != null) {
            buildFrequencyTable(node.left, s + "0");
            buildFrequencyTable(node.right, s+ "1");
        }
        else {
            dataTable.add(new FrequencyTable(node.data.symbol, s));
        }
    }





    /**
     * Class representing the node in a huffman tree. Used to build the tree.
     */
    public static class HuffData {
        private double weight;
        private Character symbol;

        public HuffData(double weigh, Character sym) {
            weight = weigh;
            symbol = sym;
        }
    }



    /**
     * Nested class for comparing two HuffmanTree's.
     */
    private static class CompareHuffmanTrees implements Comparator<BinaryTree<HuffData>> {

        /**
         * Compares the weights in both children of this root.
         *
         * @param treeLeft left child
         * @param treeRight right child
         * @return -1 if left is less than right, 0 if
         *         left equals right and +1 if left is
         *         greater than right.
         */
        @Override
        public int compare(BinaryTree<HuffData> treeLeft, BinaryTree<HuffData> treeRight) {
            double weightLeft = treeLeft.getData().weight;
            double weightRight = treeRight.getData().weight;

            return Double.compare(weightLeft, weightRight);
        }
    }


    /**
     * Stores the symbol of each character and its corresponding huffman code for easy
     * decoding lookup.
     */
    public class FrequencyTable {
        private Character symbol;
        private String frequency; //binary sequence representation of the symbol

        public FrequencyTable(Character sym, String freq) {
            symbol = sym;
            frequency = freq;
        }
    }
}

