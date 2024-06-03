package com.example.ProjectStudent.Utils;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CollectionModelMapper extends org.modelmapper.ModelMapper {

    public <S, D> List<D> mapAsList(@NonNull Iterable<S> sources, Class<D> dClass) {
        List<D> list = new ArrayList<>();
        sources.forEach(s -> list.add(super.map(s, dClass)));
        return list;
    }
}
