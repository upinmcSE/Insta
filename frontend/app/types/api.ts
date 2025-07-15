export type ApiResponse<Data> = {
  code: number;
  message: string;
  result: Data;
}