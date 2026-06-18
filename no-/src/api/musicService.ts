import client from './client'

export interface CreateMusicRequest {
  title: string
  artist: string
  description: string
  genre: string
  audio: File
  cover?: File | null
}

export interface MusicItem {
  id: number
  title: string
  artist: string
  description: string
  genre: string
  audioUrl: string
  coverImageUrl: string | null
  play: number
  likedCount: number
  liked: boolean
  nickName: string
}

export interface UpdateMusicRequest {
  id: number
  title: string
  artist: string
  description: string
  genre: string
  audioUrl: string
  coverImageUrl: string | null
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

  list(): Promise<MusicItem[]> {
    return client.get<MusicItem[]>('/music').then(res => res.data)
  },

  update(data: UpdateMusicRequest): Promise<void> {
    return client.put(`/music/${data.id}`, data)
  },

  remove(id: number): Promise<void> {
    return client.delete(`/music/${id}`)
  },

  increasePlay(id: number): Promise<void> {
    return client.post(`/music/${id}/play`)
  },

  toggleLike(id: number): Promise<void> {
    return client.post(`/favorite/music/${id}`)
  },
}
