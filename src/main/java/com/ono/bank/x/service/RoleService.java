package com.ono.bank.x.service;
import com.ono.bank.x.model.Role;
import com.ono.bank.x.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}

