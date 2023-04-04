import sqlite3


#Criar uma conexão com o servidor remoto (para quando for necessário)
#conn = sqlite3.connect('http://<REMOTE_HOST>/users.db')


#Iniciar conexão
conn = sqlite3.connect('users.db')

#Criar um objeto do tipo cursor
cursor = conn.cursor()

# Criar uma tabela para guardar a informação do usuário, se existir tabela de mesmo nome, não executa
cursor.execute('''CREATE TABLE IF NOT EXISTS users
                 (id INTEGER PRIMARY KEY, 
                 safe_id TEXT, 
                 access_level INTEGER, 
                 email TEXT, 
                 password TEXT)''')

# Adicionar usuário à tabela
def add_user(safe_id, access_level, email, password):
    cursor.execute("INSERT INTO users (safe_id, access_level, email, password) VALUES (?, ?, ?, ?)", (safe_id, access_level, email, password))
    conn.commit()
    print("User added successfully!")

# Remove usuário da tabela com o ID interno
def remove_user(user_id):
    cursor.execute("DELETE FROM users WHERE id=?", (user_id,))
    conn.commit()
    print("User removed successfully!")
    
# Remove todos os usuários da tabela 
def remove_all_users():
    cursor.execute("DELETE FROM users")
    conn.commit()
    print("All users removed successfully!")

# Testes das funções
add_user("12345", 1, "example1@gmail.com", "password1")
add_user("67890", 2, "example2@gmail.com", "password2")
add_user("252345", 3, "example3@gmail.com", "password3")

#remove_user(1)

#remove_all_users()


# Fechar conexão
conn.close()
