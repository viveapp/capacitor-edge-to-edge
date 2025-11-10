import { WebPlugin } from '@capacitor/core';

import type { EdgeToEdgePlugin, GetInsetsResult } from './definitions';



export class EdgeToEdgeWeb extends WebPlugin implements EdgeToEdgePlugin {
  async enable(): Promise<void> {
    console.error('EdgeToEdgeWeb#enable(), not implemented!');
  }

  async disable(): Promise<void> {
    console.error('EdgeToEdgeWeb#disable(), not implemented!');
  }

  async getInsets(): Promise<GetInsetsResult> {
    console.error('EdgeToEdgeWeb#getInsets(), not implemented!');
    return {
      bottom: 0,
      left: 0,
      right: 0,
      top: 0,
    };
  }

  async setBackgroundColor(): Promise<void> {
    console.error('EdgeToEdgeWeb#setBackgroundColor(), not implemented!');
  }
}
