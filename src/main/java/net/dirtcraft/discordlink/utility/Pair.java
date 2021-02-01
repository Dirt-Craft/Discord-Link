package net.dirtcraft.discordlink.utility;

public class Pair<T,S> {
    private final T t;
    private final S s;
    public Pair(T t, S s){
        this.t = t;
        this.s = s;
    }

    public T getKey(){
        return t;
    }

    public S  getValue(){
        return s;
    }
}
