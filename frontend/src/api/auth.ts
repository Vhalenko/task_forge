const API_URL = 'http://localhost:8080/api/auth'

export type ResisterRequest = {
    firstName: string
    lastName: string
    email: string
    username: string
    password: string
}

export async function registerUser(data: ResisterRequest) {
    const response = await fetch(`${API_URL}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })

    if (!response.ok) {
        throw new Error('Registration failed: ${response.status}')
    }

    if (response.status === 204) {
        return null 
    }

    return response.json()
}