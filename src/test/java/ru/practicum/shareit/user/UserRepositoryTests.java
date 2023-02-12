package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByEmail() {
        userRepository.save(User.builder().name("name").email("email@email.com").build());
        assertThat(userRepository.findUserByEmail("error@email.com").size(), equalTo(0));
        assertThat(userRepository.findUserByEmail("email@email.com").size(), equalTo(1));
    }

    @Test
    void findAllByIdNotAndEmail() {
        User user = userRepository.save(User.builder().name("name").email("email@email.com").build());
        assertThat(userRepository.findAllByIdNotAndEmail(user.getId(), user.getEmail()).size(), equalTo(0));
        assertThat(userRepository.findAllByIdNotAndEmail(user.getId() + 1, user.getEmail()).size(), equalTo(1));
    }

    @Test
    void findAllByIdNotAndName() {
        User user = userRepository.save(User.builder().name("name").email("email@email.com").build());
        assertThat(userRepository.findAllByIdNotAndName(user.getId(), user.getName()).size(), equalTo(0));
        assertThat(userRepository.findAllByIdNotAndName(user.getId() + 1, user.getName()).size(), equalTo(1));
    }
}
