import client from './client'

export interface FinalStat {
  stat_name: string
  stat_value: string
}

export interface CharacterInfo {
  character_name: string
  character_level: number
  character_class: string
  character_class_level: string
  character_image: string
  world_name: string
  character_gender: string
  character_guild_name?: string
  character_exp?: number
  character_exp_rate?: string
  popularity: number
  final_stat: FinalStat[]
  remain_ap: number
}

export const mapleService = {
  getCharacter(characterName: string): Promise<CharacterInfo> {
    return client
      .get<{ data: CharacterInfo }>('/maple/id', { params: { characterName } })
      .then(res => res.data.data)
  },
}