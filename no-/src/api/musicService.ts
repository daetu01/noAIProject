import client from './client'

export interface CreateMusicRequest {
  title: string
  artist: string
  description: string
  genre: string
  audio: File
  cover?: File | null
}

export const musicService = {
  create(data: CreateMusicRequest): Promise<void> {
    const form = new FormData()
    form.append('data', new Blob([JSON.stringify({
      title: data.title,
      artist: data.artist,
      description: data.description,
      genre: data.genre,
    })], { type: 'application/json' }))
    form.append('audio', data.audio)
    if (data.cover) {
      form.append('cover', data.cover)
    }
    return client.post('/music', form, {
      headers: { 'Content-Type': undefined },
    })
  },
}
