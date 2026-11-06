export const optionFormColType = [
  { value: 1, text: 'テキスト', select_list: false },
  { value: 2, text: 'コンボボックス（単一選択）', select_list: true },
  { value: 3, text: 'チェックボックス（複数選択）', select_list: true },
  { value: 4, text: 'ラジオボタン（単一選択）', select_list: true },
  { value: 5, text: 'テキストエリア', select_list: false },
  { value: 6, text: '隠しテキスト', select_list: false },
  { value: 7, text: '表示テキスト（非入力項目）', select_list: false }
]

export const formColTypeText = (colType) => {
  return optionFormColType.find(o => o.value === colType)?.text ?? colType
}
