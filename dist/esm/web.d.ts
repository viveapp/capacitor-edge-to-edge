import { WebPlugin } from '@capacitor/core';
import type { EdgeToEdgePlugin, GetInsetsResult } from './definitions';
export declare class EdgeToEdgeWeb extends WebPlugin implements EdgeToEdgePlugin {
    enable(): Promise<void>;
    disable(): Promise<void>;
    getInsets(): Promise<GetInsetsResult>;
    setBackgroundColor(): Promise<void>;
}
