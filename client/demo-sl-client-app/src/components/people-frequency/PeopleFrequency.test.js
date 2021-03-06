import React from 'react';
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";

import PeopleFrequency from './PeopleFrequency';

let container = null;
beforeEach(() => {
  // setup a DOM element as a render target
  container = document.createElement("div");
  document.body.appendChild(container);
});

afterEach(() => {
  // cleanup on exiting
  unmountComponentAtNode(container);
  container.remove();
  container = null;
});

it("renders button", async () => {
    act(() => {
        render(<PeopleFrequency />, container);
    });
    expect(container.querySelector("button").textContent).toBe('Show email character frequencies');
});