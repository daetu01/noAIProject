import client from './client'

export interface BoardItem {
  id: number
  title: string
  content: string
  writer: string
  uploadDir?: string
}

export interface CreateBoardRequest {
  title: string
  content: string
  writer: string
  file: File
}

export const boardService = {
  getAll(): Promise<BoardItem[]> {
    return client.get<{ data: BoardItem[] }>('/board').then(res => res.data.data)
  },

  getOne(id: number): Promise<BoardItem> {
    return client.get<{ data: BoardItem }>(`/board/${id}`).then(res => res.data.data)
  },

  create(data: CreateBoardRequest): Promise<void> {
    const form = new FormData()
    form.append('dto', new Blob([JSON.stringify({
      title: data.title,
      content: data.content,
      writer: data.writer,
    })], { type: 'application/json' }))
    form.append('file', data.file)
    return client.post('/board', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  remove(id: number): Promise<void> {
    return client.delete(`/board?id=${id}`)
  },

  like(id: number): Promise<void> {
    return client.post(`/favorite/${id}`)
  },
}
