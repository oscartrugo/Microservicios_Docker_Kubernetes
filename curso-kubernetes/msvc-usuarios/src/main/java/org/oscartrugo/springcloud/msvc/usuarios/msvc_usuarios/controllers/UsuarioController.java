package org.oscartrugo.springcloud.msvc.usuarios.msvc_usuarios.controllers;

import org.oscartrugo.springcloud.msvc.usuarios.msvc_usuarios.models.entity.Usuario;
import org.oscartrugo.springcloud.msvc.usuarios.msvc_usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerUsuario(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = service.porId(id);
        if(usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.notFound().build();
    }
}