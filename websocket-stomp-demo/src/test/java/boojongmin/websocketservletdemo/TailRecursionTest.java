package boojongmin.websocketservletdemo;

import java.util.stream.Stream;

public class TailRecursionTest {

    static class TailCalls {
        static <T> TailCall<T> call(final TailCall<T> nextCall) {
           return nextCall;
        }

        static <T> TailCall<T> done(final T value) {
            return new TailCall<T>() {
                @Override
                public TailCall<T> apply() { throw new Error("not implmented"); }

                @Override
                public boolean isComplete() { return true; }

                @Override
                public T result() { return value; }
            };

        }
    }

    interface TailCall<T> {
        TailCall<T> apply();

        default boolean isComplete() {return false;}
        default T result() { throw new Error("not implemented");}
        default T get() {
            return Stream.iterate(this, TailCall::apply)
                    .filter(TailCall::isComplete).findFirst().get().result();
        }
    }

    static TailCall factorial(int fact, int n) {
        if(n ==1)
            return TailCalls.done(fact);
        else
            return TailCalls.call(() -> factorial(fact * n, n -1));
    }



    public static void main(String[] args) {
        System.out.println(factorial(1, 5).get());

        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.right.left = new Node(4);
    }

//    static TailCall traverse(Node node) {
//        if(node == null)
//            return TailCalls.done(0);
//        else
//            return TailCalls.call(() -> );
//    }

    //---------------------
    static class Node {
        Node left;
        Node right;
        int data;
        public Node(int data) {
            this.data = data;
        }
    }
//    public static TailCall traverse(Node n) {
//
//    }
}
