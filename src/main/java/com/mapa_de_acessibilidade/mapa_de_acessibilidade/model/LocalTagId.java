package com.mapa_de_acessibilidade.mapa_de_acessibilidade.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LocalTagId implements Serializable {
    @Column(name = "local_id")
    private Long localId;

    @Column(name = "tag_id")
    private Long tagId;


     // Construtores, equals() e hashCode() são obrigatórios
    public LocalTagId(Long localId, Long tagId) {
        this.localId = localId;
        this.tagId = tagId;
    }

    public LocalTagId() {}


   @Override
    public int hashCode() {
        return Objects.hash(localId, tagId);
    } 


   @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalTagId that = (LocalTagId) o;
        return Objects.equals(localId, that.localId) &&
               Objects.equals(tagId, that.tagId);
    }


    public Long getLocalId() {
        return localId;
    }
    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public Long getTagId() {
        return tagId;
    }
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }


    
}
