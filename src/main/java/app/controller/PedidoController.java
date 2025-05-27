package app.controller;

import app.dto.PedidoDTO;

import app.entity.Pedido;

import app.entity.Usuario;
import app.service.PedidoService;
import app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;


    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<Pedido> save(@RequestBody PedidoDTO pedidoDTO) {
        Usuario comprador = usuarioService.findById(pedidoDTO.getCompradorId()).orElse(null);
        if (comprador == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Pedido pedido = pedidoService.createPedidoFromDTO(pedidoDTO, comprador);

        Pedido saved = pedidoService.save(pedido);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Pedido> update(@PathVariable("id") long id, @RequestBody PedidoDTO pedidoDTO) {
        Usuario comprador = usuarioService.findById(pedidoDTO.getCompradorId()).orElse(null);
        if (comprador == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Pedido pedido = pedidoService.createPedidoFromDTO(pedidoDTO, comprador);
        pedido.setId(id);

        Pedido atualizado = pedidoService.save(pedido);
        return new ResponseEntity<>(atualizado, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listAll")
    public ResponseEntity<List<Pedido>> listAll() {
        List<Pedido> pedidos = pedidoService.listAll();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable("id") long id) {
        return pedidoService.findById(id)
                .map(pedido -> new ResponseEntity<>(pedido, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        pedidoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
