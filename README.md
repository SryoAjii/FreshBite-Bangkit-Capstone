
# FreshBite App

FreshBite is an innovative mobile application designed to help users detect whether the fruits they have are fresh or rotten. Using advanced machine learning technology, FreshBite can analyze the condition of fruits based on visual and other sensory data, providing accurate predictions of fruit freshness.

## Features

- Authentication
- Fruit Detection
- Articles

## API Reference

#### Sign Up

```http
POST /api/auth/signup
```

Request Body:

- `"username"`: `string`, **must be unique**
- `"email"`: `string`, **must be unique and in email format**
- `"password"`: `string`, **must be at least 8 characters**

#### Sign In

```http
POST /api/auth/signin
```

Request Body:

- `"email"`: `string`, **must be in email format**
- `"password"`: `string`, **must be at least 8 characters**

#### Sign Out

```http
POST /api/auth/signout
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `x-access-token` | `string` | **Required**. Access token |

#### Get all articles

```http
GET /api/articles
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `x-access-token` | `string` | **Required**. Access token |

#### Get article by ID

```http
GET /api/articles/:id
```

| Parameter | Type | Description             |
| :-------- | :--- | :---------------------- |
| `id`      | `int` | **Required**. Article ID to fetch |
| `x-access-token` | `string` | **Required**. Access token |

#### Get articles by tag

```http
GET /api/articles/tag/:tag
```

| Parameter | Type     | Description           |
| :-------- | :------- | :-------------------- |
| `tag`     | `string` | **Required**. Tag to filter articles |
| `x-access-token` | `string` | **Required**. Access token |

#### Search articles

```http
POST /api/articles/search
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `x-access-token` | `string` | **Required**. Access token |

Request Body:

- `"title"`: `string`, **title of the article to search**

#### Predict Fruit

```http
POST /api/predict
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `x-access-token` | `string` | **Required**. Access token |

Request Body:

- `"image"`: `image`, **must be in jpeg or png format**

## Run Locally

Clone the repository

```bash
# Clone the repository
git clone https://link-to-project
```

Navigate into the project directory

```bash
# Navigate into the project directory
cd your-repository
```

Install dependencies

```bash
# Install the dependencies
npm install
```

Add environment variables

```bash
# Add environment variables
touch .env
# Open .env file and add your environment variables as needed
```

Start the server

```bash
# Start the server
node server.js
```

## Authors

**Cloud Computing**
- [Darma](https://www.github.com/darmacahya)
- [Kevin](https://github.com/Mastahcode19)

**Machine Learning**
- [Abdul](https://github.com/gamerough)
- [Adam](https://github.com/damfalah)
- [Handi](https://github.com/Nanda-Re)

**Mobile Development**
- [Aji](https://github.com/SryoAjii)
- [Gamal](https://github.com/Gamalmuhammad77)
