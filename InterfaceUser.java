public interface InterfaceUser {
    boolean createAccount(String username, String email, String password);
    boolean login(String username, String password);
    boolean deleteAccount(String username);
}