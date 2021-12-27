package eu.hansolo.toolbox.tuples;


public interface Tuple {
    int size();

    Object getValueAt(int i);

    Class getTypeAt(int i);
}
