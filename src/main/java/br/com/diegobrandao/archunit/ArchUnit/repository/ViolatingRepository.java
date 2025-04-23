//package br.com.diegobrandao.archunit.ArchUnit.repository;
//
//import br.com.diegobrandao.archunit.ArchUnit.controller.UserController;
//import br.com.diegobrandao.archunit.ArchUnit.domain.User;
//
//// Esta classe é incorreta arquiteturalmente pois um repositório não deve acessar a camada de controlador



//public class ViolatingRepository {
//    private UserController userController; // Violação: Um Repository não deve conhecer Controllers
//
//    public User getViaController(Long id) {
//        return userController.getUser(id).orElse(null); // Violação: acesso direto ao controlador
//    }
//}


//      CASO QUEIRA VER O ARCHUNIT MOSTRANDO ERRO, DESCOMENTE ESSE CÓDIGO