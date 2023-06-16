package ra.project.service;

import ra.project.model.User;

import java.util.List;
import java.util.Optional;

public interface IGenerateService <T,E>{
    List<T> findAll();
    T findById(E id);
    T save(T t);
    void deleteById(E id);
}
