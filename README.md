# Admin App

## Setup Instructions

To run the application, follow the steps below:

### 1. Export Environment Variables
First, export the environment variables from the `app.env` file.

#### For Ubuntu:

1. Open the terminal and run:

    ```bash
    nano ~/.bashrc
    ```

2. At the end of the file, add the environment variables from `app.env`. For example:

    ```bash
    DB_USERNAME=your_db_username
    DB_PASSWORD=your_db_password
    ```

3. Save the changes and exit the file.

4. Apply the changes by running:

    ```bash
    source ~/.bashrc
    ```

5. Verify that the environment variables have been set correctly by running the following commands (example):

    ```bash
    echo $DB_USERNAME
    echo $DB_PASSWORD
    ```

### 2. Run the Database

You can either:

- **Run the database via Docker** using the provided `docker-compose.yml` file:

    ```bash
    docker-compose up -d
    ```

- **Run the database manually** basing on env variables from app.env file.

### 3. Run the Application

Then you can start application in IDE.

