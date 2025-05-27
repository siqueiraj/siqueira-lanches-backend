package app.controller;

import java.util.List;

import app.dto.PagamentoDTO;
import app.entity.Pagamento;
import app.entity.Pedido;
import app.service.PagamentoService;
import app.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PedidoService pedidoService;

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody PagamentoDTO dto) {
        Pedido pedido = pedidoService.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return new ResponseEntity<>("Pedido não encontrado", HttpStatus.BAD_REQUEST);
        }

        if (pagamentoService.findByPedidoId(dto.getPedidoId()).isPresent()) {
            return new ResponseEntity<>("Pagamento já existe para este pedido", HttpStatus.CONFLICT);
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(dto.getValor() != null ? new java.math.BigDecimal(dto.getValor()) : null);

        Pagamento salvo = pagamentoService.save(pagamento);
        return new ResponseEntity<>(salvo, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Pagamento> update(@PathVariable("id") long id, @RequestBody PagamentoDTO dto) {
        Pedido pedido = pedidoService.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setId(id);
        pagamento.setPedido(pedido);
        pagamento.setValor(dto.getValor() != null ? new java.math.BigDecimal(dto.getValor()) : null);

        Pagamento atualizado = pagamentoService.save(pagamento);
        return new ResponseEntity<>(atualizado, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listAll")
    public ResponseEntity<List<Pagamento>> listAll() {
        List<Pagamento> pagamentos = pagamentoService.listAll();
        return new ResponseEntity<>(pagamentos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<Pagamento> findById(@PathVariable("id") long id) {
        return pagamentoService.findById(id)
                .map(pagamento -> new ResponseEntity<>(pagamento, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        pagamentoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
