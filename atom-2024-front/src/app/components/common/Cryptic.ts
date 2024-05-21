import CryptoJS from "crypto-js";

export class Cryptic {
  static encode(value: string): string {
    if (value.length > 0)
      return CryptoJS.SHA256(value).toString(CryptoJS.enc.Base64);
    else
      return "";
  }
}
