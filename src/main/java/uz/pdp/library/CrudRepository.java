package uz.pdp.library;

public interface CrudRepository<T> {

    void create();

    void read();

    void update();

    void delete();

    T findById();

    T filter(String object);

    void writeJson();

    void crudMenu();

}
