import {Component, Input} from '@angular/core';
import {VgApiService, VgCoreModule} from "@videogular/ngx-videogular/core";
import {VgControlsModule} from "@videogular/ngx-videogular/controls";
import {VgOverlayPlayModule} from "@videogular/ngx-videogular/overlay-play";
import {VgBufferingModule} from "@videogular/ngx-videogular/buffering";

@Component({
  selector: 'app-video-player',
  standalone: true,
  imports: [
    VgCoreModule,
    VgControlsModule,
    VgOverlayPlayModule,
    VgBufferingModule],
  templateUrl: './video-player.component.html',
  styleUrl: './video-player.component.css'
})
export class VideoPlayerComponent {
  api: VgApiService;

  @Input() mp4SourceUrl: string;

  private _isActive: boolean;
  @Input()  get isActive(): boolean {
    return this._isActive;
  }

  set isActive(value: boolean) {
    this._isActive = value;
    if (!!this.api && !value) {
      this.api.pause();
    }
  }

  onPlayerReady(api: VgApiService) {
    this.api = api;
  }
}
